package com.bbailey.smarthome.zwave;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import com.bbailey.smarthome.zwave.api.SoftReset;
import com.bbailey.smarthome.zwave.api.common.Command;
import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.CommandSequence;
import com.bbailey.smarthome.zwave.api.common.CommandSequence.SequenceState;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.NoResponse;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.AcknowledgeFrame;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.protocol.CancelFrame;
import com.bbailey.smarthome.zwave.protocol.DataFrame;
import com.bbailey.smarthome.zwave.protocol.DataFrame.CommandType;
import com.bbailey.smarthome.zwave.protocol.NonAcknowledgeFrame;
import com.bbailey.smarthome.zwave.protocol.ZwaveFrame;
import com.bbailey.smarthome.zwave.protocol.ZwaveFrame.FrameType;
import com.bbailey.smarthome.zwave.utils.BitUtils;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;



public class ZwaveAdapter implements SmartLifecycle {

	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ZwaveAdapter.class);
	
	private final SerialIoHandler serialIoHandler;
	private final ExecutorService executor;
	private final BlockingQueue<Command> receivedQueue = new ArrayBlockingQueue<>(1000);
	
	private boolean running = false;
	
	private final Map<Integer, CommandMetaData> commandMap = new HashMap<>();
	
	private CommandSequence inflightSequence = null;
	
	private volatile int sessionId = 1;
	
	private Set<Subscription<? extends Command>> subscriptions = new HashSet<>();
	
	public ZwaveAdapter(SerialPort serialPort) {
		
		this.serialIoHandler = new SerialIoHandler(serialPort);
		
		executor = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "event-listener");
			}
			
		});

		
		
		/*
		 * Setup the meta data needed to understand how to route responses.
		 * This is taken from section 4.3 of the Z-Wave Host API Specification doc
		 */
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(CommandFrame.class));
		
		Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(this.getClass().getPackageName());
		for (BeanDefinition beanDefinition : beanDefinitions) {
			
			LOGGER.info("Class name: {}", beanDefinition.getBeanClassName());
			try {
				Class<?> clazz = ClassUtils.getDefaultClassLoader().loadClass(beanDefinition.getBeanClassName());
				CommandFrame commandFrame = clazz.getAnnotation(CommandFrame.class);
				CommandMetaData metaData = new CommandMetaData(
						commandFrame.flow(),
						commandFrame.requestClass(),
						commandFrame.responseClass(),
						commandFrame.callbackClass()
					);
				LOGGER.info("Registering command: {}, req: {}, resp: {}", BitUtils.toHex(commandFrame.value()), commandFrame.requestClass().getSimpleName(), commandFrame.responseClass().getSimpleName());
				commandMap.put(commandFrame.value(), metaData);
			} catch (Exception e) {
				LOGGER.error("Failed to load class {} - {}", beanDefinition.getBeanClassName(), e.getMessage());
			}
		}
	
	}
	
	
	/**
	 * Get the next session Id to use in a message
	 * @return The session Id between 1-0xFF
	 */
	public synchronized int nextSessionId() {
		
		int s = sessionId++;
		if (sessionId > 0xFF) {
			sessionId = 1;
		}
		
		return s;
	}
	
	public <T extends Command> void subscribe(Class<T> clazz, Consumer<T> consumer) {
		subscribe(clazz, consumer, null);
	}
	
	public <T extends Command> void subscribe(Class<T> clazz, Consumer<T> consumer, Predicate<T> filter) {
		Subscription<T> subscription = new Subscription<>(clazz, consumer, filter);
		subscriptions.add(subscription);
	}
	

	
	@Override
	public void start() {
		LOGGER.info("Starting the Zwave adapter");
		running = true;
		serialIoHandler.openPort();
		Runnable notifier = new Runnable() {

			@Override
			public void run() {
				
				while (running) {
					Command command;
					try {
						command = receivedQueue.poll(100, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
					if (command != null) {
						LOGGER.info("Notifying listeners for {}", command);
						notifyListeners(command);
					}
				}
				
			}
			
		};
		
		executor.submit(notifier);
	}


	@Override
	public void stop() {
		
		LOGGER.info("Stopping the Zwave adapter");
		running = false;
		
		try {
			if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				executor.shutdown();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		if (!serialIoHandler.closePort()) {
			LOGGER.warn("Failed to close serial-port on shutdown");
		}
	}


	@Override
	public boolean isRunning() {
		return running;
	}
	
	
	/**
	 * Send a command to the zwave module and wait for the appropriate
	 * response
	 * @param request The request to send
	 * @return The completed command sequence with any responses
	 * or callback data included
	 */
	public void sendCommand(SerializableCommand request) {
		
		CommandMetaData metaData = commandMap.get(request.getCommandId());
		if (metaData == null) {
			throw new IllegalArgumentException("No command mapping for: " + BitUtils.toHex(request.getCommandId()));
		}
		
		CommandSequence sequence = new CommandSequence(request, metaData.getFlow());
		inflightSequence = sequence;
		
		// move it into the started state
		sequence.startSequence();
		while (sequence.canTransmit()) {
				
			LOGGER.info("Command {}: attempt {} / {}", BitUtils.toHex(request.getCommandId()), sequence.getAttempt(), sequence.getNumRetries());
			serialIoHandler.sendFrame(new DataFrame(sequence.getRequest()));
			
			// Wait for the command the complete
			if (!sequence.waitForCompletion(30000, TimeUnit.MILLISECONDS)) {
				if (sequence.getState() == SequenceState.WAITING_FOR_ACK) {
					LOGGER.warn("Timed out waiting for ACK - retrying");
					sequence.retry();
				}
				// TODO: Need to handle callbacks delays being longer.
				break;
			} else {
				break;
			}
		}
		
		// If still not complete after retries issue a soft reset
		if (!sequence.isComplete() && request.getCommandId() != SoftReset.COMMAND_ID) {
			//reset();
		}
	}
	

	/**
	 * Notify all subscriptions that we have received a command message
	 * @param <T>
	 * @param command The command message
	 */
	private <T extends Command> void notifyListeners(T command) {

		LOGGER.info("Received: {}", command);
		
		for (Subscription<? extends Command> subscription : subscriptions) {
			if (command.getClass().equals(subscription.getClazz())) {
				subscription.handle(command);
			}
		}
	}
	
	
	private void handleACK(AcknowledgeFrame frame) {
		
		if (inflightSequence == null) {
			LOGGER.warn("Received unexpected ACK");
		} else {
			inflightSequence.acknowledgeRequest();
		}
	}
	
	private void handleNAK(NonAcknowledgeFrame frame) {
		LOGGER.info("NAK received");
	}
	
	private void handleSOF(DataFrame frame) {
		
		int expectedChecksum = frame.calculateChecksum();
		if (expectedChecksum != frame.getChecksum()) {
			// Checksum does not match, send a NAK frame to trigger a resend
			LOGGER.warn("Invalid checksum: received {} - expected {}", BitUtils.toHex(frame.getChecksum()), BitUtils.toHex(expectedChecksum));
			serialIoHandler.sendFrame(ZwaveFrame.of(FrameType.NAK));
			return;
		}
		
		// The frame is valid so we should ACK
		serialIoHandler.sendFrame(ZwaveFrame.of(FrameType.ACK));
		
		CommandMetaData metaData = commandMap.get(frame.getCommandId());
		if (metaData == null) {
			LOGGER.info("Received data-frame for unknown command {} - discarding", BitUtils.toHex(frame.getCommandId()));
			return;
		}
		
		Class<? extends Command> clazz = metaData.getAppropriateClass(frame.getType());
		Constructor<? extends Command> constructor;
		try {
			constructor = clazz.getConstructor(Buffer.class);
		} catch (NoSuchMethodException | SecurityException e) {
			LOGGER.error("Failed to find constructor for {} - {}", clazz.getSimpleName(), e.getMessage());
			throw new RuntimeException(e);
		}
		
		Command command;
		try {
			command = constructor.newInstance(frame.getCommandPayload());
		} catch (Exception e) {
			LOGGER.error("Failed to constructor new instance of {} - {}", clazz.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
		
		receivedQueue.add(command);
		
		if (inflightSequence == null) {
			LOGGER.info("Received unsolicited command {} - discarding", BitUtils.toHex(command.getCommandId()));
			return;
		} else {
			
			if (frame.getType() == CommandType.REQUEST) {
				if (metaData.getFlow().hasCallback()) {
					// Mark that we have received a callback
					inflightSequence.updateCallback(command);
				}
			} else {
				inflightSequence.updateResponse(command);
			}
		}
	}
	
	private void handleCAN(CancelFrame frame) {
		LOGGER.info("CAN received");
	}
	
	private void logFrame(String prefix, byte[] buffer) {
		String[] hex = new String[buffer.length];
		for (int i = 0 ; i < buffer.length ; i++) {
			hex[i] = BitUtils.toHex(buffer[i]);
		}
		
		LOGGER.info("{} - [{}]", prefix, Strings.join(Arrays.asList(hex), ','));
	}
	

	private class SerialIoHandler implements SerialPortDataListener {
		
		private enum ReadState {
			WAITING_FOR_FRAME,
			WAITING_FOR_LENGTH,
			READING_FRAME;
		}
		
		private final SerialPort serialPort;
		private final ReentrantLock lock = new ReentrantLock();
		
		private final AdapterStatistics rxStats = new AdapterStatistics();
		private final AdapterStatistics txStats = new AdapterStatistics();
		
		private ReadState state = ReadState.WAITING_FOR_FRAME;
		private int expectedLength = -1;
		private int bytesReceived = 0;
		private byte[] buffer;
		
		
		public SerialIoHandler(SerialPort serialPort) {
			this.serialPort = serialPort;
			this.serialPort.addDataListener(this);
			
		}
		
		public boolean openPort() {
			boolean isOpen = serialPort.openPort(100);
			if (!isOpen) {
				LOGGER.error("Failed to open serial port");
				return false;
			}
			
			// Send a NAK to reset any partial transaction
			sendFrame(ZwaveFrame.of(FrameType.NAK));
			return true;
		}
		
		
		public boolean closePort() {
			serialPort.removeDataListener();
			return serialPort.closePort();
		}
		
		
		
		/**
		 * @return the state
		 */
		private ReadState getState() {
			return state;
		}

		/**
		 * @param state the state to set
		 */
		private void setState(ReadState state) {
			this.state = state;
			
			if (state != ReadState.WAITING_FOR_FRAME && !lock.isHeldByCurrentThread()) {
				LOGGER.info("READ: Locking");
				lock.lock();
				LOGGER.info("READ: Locked");
			}
			
			if (state == ReadState.WAITING_FOR_FRAME && lock.isHeldByCurrentThread()) {
				LOGGER.info("READ: Unlocking");
				lock.unlock();
				LOGGER.info("READ: unlocked");
			}

		}

		public void sendFrame(ZwaveFrame frame) {
			
			byte[] packet = frame.serialize();
			
				
			try {
				
				LOGGER.info("WRITE: Locking");
				lock.lock();
				LOGGER.info("WRITE: Locked");
				
				if (frame.getFrameType() == FrameType.SOF) {
					logFrame("SEND", packet);
				} else {
					LOGGER.info("SEND {}", frame.getFrameType());
				}
				
				txStats.record(frame.getFrameType());
				serialPort.flushIOBuffers();
				serialPort.getOutputStream().write(packet);
				serialPort.getOutputStream().flush();
			} catch (IOException e) {
				LOGGER.error("Failed to send frame - {}", e.getMessage());
			} finally {
				if (lock.isHeldByCurrentThread()) {
					LOGGER.info("WRITE: Unlocking");
					lock.unlock();
					LOGGER.info("WRITE: Unlocked");
				}
			}
		}
		
		private void handleByte(int nextByte) {
			
			LOGGER.info("Received byte {} : {}", BitUtils.toHex(nextByte), getState());
			
			switch (getState()) {
			case WAITING_FOR_FRAME:
				// Start a new frame
				FrameType frameType = FrameType.fromProtocolValue(nextByte).orElse(null);
				if (frameType == FrameType.SOF) {
					setState(ReadState.WAITING_FOR_LENGTH);
				} else {
					// All other frames are just a single byte
					setState(ReadState.WAITING_FOR_FRAME);
					if (frameType == null) {
						// This is an out-of-frame byte. We will ignore
						LOGGER.warn("Received byte {} out of frame - ignoring", BitUtils.toHex(nextByte));
						rxStats.recordOutOffFrame();
					} else {
						onFrameReceived(ZwaveFrame.of(frameType));
					}
				}
				break;
			case WAITING_FOR_LENGTH:
				// Read the frame length
				expectedLength = nextByte;
				if (expectedLength < 4 || expectedLength > 64) {
					LOGGER.error("Frame length {} is not valid", expectedLength);
				}
				buffer = new byte[expectedLength];
				bytesReceived = 0;
				setState(ReadState.READING_FRAME);
				break;
			case READING_FRAME:
				buffer[bytesReceived++] = (byte)(nextByte & 0xFF);
				if (bytesReceived == expectedLength) {
					// This is the full frame received
					setState(ReadState.WAITING_FOR_FRAME);
					logFrame("REC", buffer);
					onFrameReceived(new DataFrame(expectedLength, buffer));
				}
				break;
			}
		}
		
		
		
		
		private void onFrameReceived(ZwaveFrame frame) {
			
			rxStats.record(frame.getFrameType());
			
			switch (frame.getFrameType()) {
			case ACK:
				handleACK((AcknowledgeFrame)frame);
				break;
			case CAN:
				handleCAN((CancelFrame)frame);
				break;
			case NAK:
				handleNAK((NonAcknowledgeFrame)frame);
				break;
			case SOF:
				handleSOF((DataFrame)frame);
				break;
			default:
				throw new IllegalArgumentException("Insupported FrameType: " + frame.getFrameType());
			}
		}
		
		
		private void onFrameError() {	
			// Send a NAK to reset things
			sendFrame(ZwaveFrame.of(FrameType.NAK));
		}
		
		
		@Override
		public int getListeningEvents() {
			Thread.currentThread().setName("serial-receive");
			return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			
			LOGGER.info("Received Serial Event - {} bytes", event.getReceivedData().length);
			for (byte b : event.getReceivedData()) {
				try {
					handleByte(b);
				} catch (Exception e) {
					LOGGER.error("Failed to parse frame - {}", e);
					onFrameError();
					break;
				}
			}
			LOGGER.info("Completed Serial Event");
			
		}
		
	}


	private static class CommandMetaData {
		
		private final CommandFlow flow;
		
		// Request that we initiaite must be serializable
		// Unsolicited requests should be deserializable
		private final Class<? extends Command> requestClass;
		private final Class<? extends DeserializableCommand> responseClass;
		private final Class<? extends DeserializableCommand> callbackClass;
		
		
		/**
		 * @return the flow
		 */
		public CommandFlow getFlow() {
			return flow;
		}
		


		public CommandMetaData(
				CommandFlow flow, 
				Class<? extends Command> requestClass, 
				Class<? extends DeserializableCommand> responseClass, 
				Class<? extends DeserializableCommand> callbackClass
		) {
			super();
			this.flow = flow;
			this.requestClass = requestClass;
			if (NoResponse.class.equals(responseClass)) {
				// The NoResponse class is used to indicate there is no expected response
				this.responseClass = null;
			} else {
				this.responseClass = responseClass;
			}
			
			if (NoResponse.class.equals(callbackClass)) {
				// The NoResponse class is used to indicate there is no expected callback
				this.callbackClass = null;
			} else {
				this.callbackClass = callbackClass;
			}
			
			/*
			 * Validate that the given classes match the definition expected by this flow
			 */
			if (flow.hasResponse() && this.responseClass == null) {
				throw new IllegalArgumentException("A response class is expected for flow " + flow);
			}
			
			if (!flow.hasResponse() && this.responseClass != null) {
				throw new IllegalArgumentException("No response is expected for a flow of " + flow);
			}
			
			if (flow.hasCallback() && this.callbackClass == null) {
				throw new IllegalArgumentException("A callback class is expected for flow " + flow);
			}
			
			if (!flow.hasCallback() && this.callbackClass != null) {
				throw new IllegalArgumentException("No callback is expected for a flow of " + flow);
			}
			
			if (this.requestClass == null) {
				throw new IllegalArgumentException("A request class is expected for flow " + flow);
			}
			
			if (flow == CommandFlow.UNSOLICITED && !DeserializableCommand.class.isAssignableFrom(this.requestClass)) {
				throw new IllegalArgumentException("UNSOLICTED requests must be DeserializableCommand classes");
			}
			
			/*
			 * Validate that the Buffer constructor exists. We can't validate this through the
			 * class system so we will do a quick check on startup
			 */
			if (DeserializableCommand.class.isAssignableFrom(this.requestClass)) {
				validateConstructor(this.requestClass);
			}
			if (this.responseClass != null) {
				validateConstructor(this.responseClass);
			}
			if (this.callbackClass != null) {
				validateConstructor(this.callbackClass);
			}
			
		}
		
		
		public Class<? extends Command> getAppropriateClass(CommandType commandType) {
			
			if (commandType == CommandType.RESPONSE) {
				return responseClass;
			}
			
			// For requests, it could either be the CALLBACK or the REQUEST class based on the flow type
			if (flow == CommandFlow.UNSOLICITED) {
				return requestClass;
			} else {
				return callbackClass;
			}
		}
		
		
		private void validateConstructor(Class<? extends Command> clazz) {
			try {
				clazz.getConstructor(Buffer.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " must have a Buffer constructor");
			}
		}
	}
	
	
	public static class Subscription<T extends Command> {
		
		private final Class<T> clazz;
		private final Consumer<T> consumer;
		private final Predicate<T> filter;
		
		
		/**
		 * @return the clazz
		 */
		public Class<T> getClazz() {
			return clazz;
		}
		/**
		 * @return the consumer
		 */
		public Consumer<T> getConsumer() {
			return consumer;
		}
		
		
		public Subscription(Class<T> clazz, Consumer<T> consumer, Predicate<T> filter) {
			this.clazz = clazz;
			this.consumer = consumer;
			this.filter = filter;
			
		}
		
		public void handle(Object record) {
			T command = clazz.cast(record);
			if (filter == null || filter.test(command)) {
				this.consumer.accept(command);
			}
		}
	}
	
	
	public static class AdapterStatistics {
		
		private int sof = 0;
		private int ack = 0;
		private int nak = 0;
		private int can = 0;
		private int oof = 0;
		

		/**
		 * Get the number of data packets (SOF)
		 * @return the sof
		 */
		public int getSof() {
			return sof;
		}


		/**
		 * Get the number of ACK packets
		 * @return the ack
		 */
		public int getAck() {
			return ack;
		}
		
		
		/**
		 * Get the number of NAK packets
		 * @return the nak
		 */
		public int getNak() {
			return nak;
		}
		
		
		/**
		 * Get the number of CAN packets
		 * @return the can
		 */
		public int getCan() {
			return can;
		}
		
		
		/**
		 * Get the number of out of frame bytes
		 * @return the oof
		 */
		public int getOof() {
			return oof;
		}
		
		
		public void record(FrameType frameType) {
			switch (frameType) {
			case ACK:
				ack++;
				break;
			case CAN:
				can++;
				break;
			case NAK:
				nak++;
				break;
			case SOF:
				sof++;
				break;
			default:
				break;
			}
		}
		
		
		public void recordOutOffFrame() {
			oof++;
		}


		@Override
		public String toString() {
			return "AdapterStatistics [sof=" + sof + ", ack=" + ack + ", nak=" + nak + ", can=" + can + ", oof=" + oof
					+ "]";
		}
	}
}

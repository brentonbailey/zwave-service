package com.bbailey.smarthome.zwave.device;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import com.bbailey.smarthome.zwave.ZwaveAdapter;
import com.bbailey.smarthome.zwave.api.AddNodeToNetwork;
import com.bbailey.smarthome.zwave.api.AddNodeToNetwork.Request.Mode;
import com.bbailey.smarthome.zwave.api.ApiStarted;
import com.bbailey.smarthome.zwave.api.ApplicationUpdate;
import com.bbailey.smarthome.zwave.api.GetBackgroundRssi;
import com.bbailey.smarthome.zwave.api.GetCapabilities;
import com.bbailey.smarthome.zwave.api.GetControllerCapabilities;
import com.bbailey.smarthome.zwave.api.GetControllerCapabilities.ControllerCapabilities;
import com.bbailey.smarthome.zwave.api.GetInitData;
import com.bbailey.smarthome.zwave.api.GetLibraryVersion;
import com.bbailey.smarthome.zwave.api.GetNetworkIds;
import com.bbailey.smarthome.zwave.api.GetNodeInformationProtocolData;
import com.bbailey.smarthome.zwave.api.GetSucNodeId;
import com.bbailey.smarthome.zwave.api.RequestNodeInformation;
import com.bbailey.smarthome.zwave.api.SoftReset;
import com.bbailey.smarthome.zwave.api.common.Command;
import com.bbailey.smarthome.zwave.api.common.LibraryType;
import com.bbailey.smarthome.zwave.api.common.Version;
import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * Represents the Zwave controller in the network.
 */
public class ZwaveController extends ZwaveDevice implements SmartLifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZwaveController.class);
	
	public enum State {
		STARTED,
		RESTARTING
	}
	
	
	public enum DiscoveryState {
		SEARCHING,
		STOPPPED
	}
	
	private boolean running = false;
	
	/*
	 * Controller fields
	 */
	private Version libraryVersion;
	
	private LibraryType libraryType;
	private int sucNodeId;
	
	private Version apiVersion;
	private Set<Integer> supportedCommands;
	
	private EnumSet<ControllerCapabilities> controllerCapabilities;
	
	private State state = State.STARTED;
	private DiscoveryState discoveryState = DiscoveryState.STOPPPED;

	
	// The nodes in the network
	private Set<ZwaveNode> nodes = new HashSet<>();
	
	private AtomicInteger sessionTracker = new AtomicInteger();
	
	public ZwaveController(ZwaveAdapter adapter) {
		super(adapter, "controller");
		adapter.subscribe(GetNetworkIds.Response.class, (command) -> {
			setHomeId(command.getHomeId());
			setNodeId(command.getNodeId());
		});
		adapter.subscribe(GetLibraryVersion.Response.class, (command) -> {
			this.libraryType = command.getLibraryType();
			this.libraryVersion = command.getLibraryVersion();
		});
		adapter.subscribe(GetSucNodeId.Response.class, (command) -> {
			this.sucNodeId = command.getSucNodeId();
		});
		adapter.subscribe(GetControllerCapabilities.Response.class, (command) -> {
			this.controllerCapabilities = command.getCapabilities();
		});
		adapter.subscribe(GetCapabilities.Response.class, (command) -> {
			apiVersion = command.getApiVersion();
			setManufacturerId(command.getManufacturerId());
			setDeviceId(command.getProductId());
			setDeviceType(command.getProductType());
			supportedCommands = command.getSupportedCommands();
		});
		adapter.subscribe(GetInitData.Response.class, (command) -> {
			for (int nodeId : command.getNodes()) {
				addNode(nodeId);
			}
		});
		adapter.subscribe(ApiStarted.Request.class, this::processApiStarted);
		adapter.subscribe(AddNodeToNetwork.Callback.class, this::processAddNodeCallback);
		
	}
	
	
	/**
	 * Initiate a soft-reset
	 */
	public void reset() {
		
		LOGGER.info("Initiating a soft-reset of the controller");
		send(new SoftReset.Request());
		state = State.RESTARTING;
	}
	

	
	public boolean isCommandSupported(int commandId) {
		return this.supportedCommands.contains(commandId);
	}
	
	
	public void runDiscovery() {
		
		
		DiscoverySession discoverySession = new DiscoverySession(60, TimeUnit.SECONDS);
		discoverySession.start();
	}
	
	private void initialise() {
		
		LOGGER.info("Initializing the zwave controller");
		
		/*
		 * Need to send the following messages to initialise the controller
		 * GetLibraryVersion
		 * MemoryGetId
		 * GetSucNodeId
		 * GetControllerCapabilities
		 * SerialApiGetCapabilities
		 * SerialApiGetInitData
		 */
		
		LOGGER.info("Performing a soft reset");
		send(new SoftReset.Request());
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("Triggering initialisation");
		send(new GetLibraryVersion.Request());
		send(new GetNetworkIds.Request());
		send(new GetSucNodeId.Request());
		send(new GetControllerCapabilities.Request());
		send(new GetCapabilities.Request());
		send(new GetBackgroundRssi.Request());
		
		// Find out all of the nodes that the controller knows about so we can build our view of the network
		send(new GetInitData.Request());
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//runDiscovery();
	}
	
	
	/**
	 * Process an unsolicited application update message
	 * @param request The unsolicted request
	 */
	private void processApiStarted(ApiStarted.Request request) {
		
		LOGGER.info("API has started: {}", request.getWakeupReason());
		state = State.STARTED;
	}
	
	
	private void processAddNodeCallback(AddNodeToNetwork.Callback callback) {
		
	}
	


	
	private void addNode(int nodeId) {
		
		if (nodeId == getNodeId()) {
			// Cannot add ourselves to the network
			return;
		}
		
		// Add a node
		LOGGER.info("Adding node {} to network", BitUtils.toHex(nodeId));
		ZwaveBootstrappingNode node = new ZwaveBootstrappingNode(getAdapter(), nodeId);
		nodes.add(node);
		
		node.startBootstrapProcess();
	}
	


	@Override
	public void start() {
		this.running = true;
		initialise();
	}

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
	
	
	
	private class DiscoverySession {
		
		private final int sessionId;
		private final Timer timer;
		
		private DiscoveryState state = DiscoveryState.STOPPPED;
		
		public DiscoverySession(long timeout, TimeUnit unit) {
			this.sessionId = sessionTracker.getAndIncrement();
			this.timer = new Timer("device-discover");
		}
		
		public void start() {
			
			LOGGER.info("Starting discovery process...");
			
			boolean networkWideInclusion = false;
			AddNodeToNetwork.Request request = new AddNodeToNetwork.Request(true, networkWideInclusion, Mode.ANY, sessionId);
			send(request);
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					stop();
				}
			};
			timer.schedule(task, 60000);
		}
		
		
		public void stop() {
			
			LOGGER.info("Stopping discovery process...");
			
			boolean networkWideInclusion = false;
			AddNodeToNetwork.Request request = new AddNodeToNetwork.Request(true, networkWideInclusion, Mode.STOP_INCLUSION, sessionId);
			send(request);
			timer.cancel();
		}
		
	}
}

package com.bbailey.smarthome.zwave.device;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.ZwaveAdapter;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.utils.BitUtils;

public abstract class ZwaveDevice {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final ZwaveAdapter adapter;
	
	private int nodeId;
	private int homeId;
	private int manufacturerId;
	private int deviceId;
	private int deviceType;
	
	private BlockingQueue<SerializableCommand> sendQueue = new ArrayBlockingQueue<>(1000);
	
	private final ExecutorService executor;
	
	
	/**
	 * @return the adapter
	 */
	public ZwaveAdapter getAdapter() {
		return adapter;
	}
	
	/**
	 * The node Id within the zwave mesh network
	 * @return the nodeId
	 */
	public int getNodeId() {
		return nodeId;
	}
	
	
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	
	/**
	 * The homeId representing the network
	 * @return the homeId
	 */
	public int getHomeId() {
		return homeId;
	}
	
	
	/**
	 * @param homeId the homeId to set
	 */
	public void setHomeId(int homeId) {
		this.homeId = homeId;
	}
	
	
	/**
	 * The manufacturer Id for the device
	 * @return the manufacturerId
	 */
	public int getManufacturerId() {
		return manufacturerId;
	}
	
	
	/**
	 * @param manufacturerId the manufacturerId to set
	 */
	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	
	
	/**
	 * The manufacturers Id for the device
	 * @return the deviceId
	 */
	public int getDeviceId() {
		return deviceId;
	}
	
	
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	
	
	/**
	 * The device type
	 * @return the deviceType
	 */
	public int getDeviceType() {
		return deviceType;
	}
	
	
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	
	public ZwaveDevice(ZwaveAdapter adapter, String name) {
		this.adapter = adapter;
		
		ThreadFactory factory = new ThreadFactory() {
			private final String threadName = name;
			
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, threadName + "-send");
			}
		};
		this.executor = Executors.newSingleThreadExecutor(factory);
		this.executor.submit(new SendThread());
	}
	
	
	public void send(SerializableCommand command) {
		sendQueue.add(command);
	}

	@Override
	public String toString() {
		return "ZwaveDevice [nodeId=" + BitUtils.toHex(nodeId) + ", homeId=" + BitUtils.toHex(homeId) + "]";
	}

	private class SendThread implements Runnable {

		
		@Override
		public void run() {
			
			LOGGER.info("Starting send-thread");
			
			while (true) {
				
				SerializableCommand command;
				try {
					command = sendQueue.poll(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				if (command != null) {
					LOGGER.info("Sending command: {}", BitUtils.toHex(command.getCommandId()));
					try {
						adapter.sendCommand(command);
					} catch (Exception e) {
						LOGGER.error("Failed to send command {} - {}", BitUtils.toHex(command.getCommandId()), e);
					}
				}
			}
		}
		
	}
}

package com.bbailey.smarthome.zwave.commandclass;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.api.ControllerNodeSendData;
import com.bbailey.smarthome.zwave.api.common.TxOptions;
import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

public abstract class CommandClass {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final int commandId;
	private final int supportedVersion;
	private final ZwaveNode node;
	
	private final Map<Integer, Method> receivers = new HashMap<>();

	
	/**
	 * The command class Id
	 * @return the commandId
	 */
	public int getCommandId() {
		return commandId;
	}
	
	/**
	 * @return the supportedVersion
	 */
	public int getSupportedVersion() {
		return supportedVersion;
	}

	/**
	 * The node this command class is bound to
	 * @return the node
	 */
	protected ZwaveNode getNode() {
		return node;
	}
	
	
	
	protected CommandClass(int commandId, int versionId, ZwaveNode node) {
		super();
		this.commandId = commandId;
		this.supportedVersion = versionId;
		this.node = node;
		
		// Create the receive mappers
		for (Method method : this.getClass().getDeclaredMethods()) {
			ReportHandler handler = method.getAnnotation(ReportHandler.class);
			if (handler != null) {
				method.trySetAccessible();
				receivers.put(handler.id(), method);
			}
		}
		
	}



	/**
	 * Send the command class to the zwave device
	 * @param buffer The serialized command including the command class Id
	 */
	protected void send(Buffer buffer) {
		ControllerNodeSendData.Request request = new ControllerNodeSendData.Request(
				node.getNodeId(), 
				buffer, 
				EnumSet.of(TxOptions.TRANSMIT_OPTION_ACK), 
				node.getAdapter().nextSessionId()
			);
		
		node.send(request);
	}
	
	
	/**
	 * Process the application update that resulted from this command class
	 * @param buffer The buffer that has already consumed the command class Id
	 */
	public void receive(Buffer buffer) {
		
		int cmd = buffer.next();
		Method method = receivers.get(cmd);
		if (method == null) {
			LOGGER.info("Received an unsupported cmd {} - ignoring", BitUtils.toHex(cmd));
			return;
		}
		
		try {
			method.invoke(this,  buffer);
		} catch (Exception e) {
			LOGGER.error("Failed to process cmd {} - {}", BitUtils.toHex(cmd), e);
		}
	}


	@Override
	public int hashCode() {
		return Objects.hash(commandId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommandClass other = (CommandClass) obj;
		return commandId == other.commandId;
	}


	@Override
	public String toString() {
		return "CommandClass [commandId=" + BitUtils.toHex(commandId) + "]";
	}
	

}

package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * 4.4.1.4 RequestNodeInformationCommand
 * 
 * This command is used to request the Node Information Frame from a Z-Wave Node
 */
@CommandFrame(
		value = RequestNodeInformation.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE,
		requestClass = RequestNodeInformation.Request.class,
		responseClass = RequestNodeInformation.Response.class
	)
public class RequestNodeInformation {

	public static final int COMMAND_ID = 0x60;
	
	public static class Request extends SerializableCommand {
		
		private final int nodeId;
		
		public Request(int nodeId) {
			super(COMMAND_ID);
			this.nodeId = nodeId;
		}
		
		@Override
		public Buffer serialize() {
			return Buffer.of(nodeId);
		}

		@Override
		public String toString() {
			return "RequestNodeInformation.Request [nodeId=" + BitUtils.toHex(nodeId) + "]";
		}
	}
	
	
	public static class Response extends DeserializableCommand {
		
		private final boolean commandSuccessful;
		
		/**
		 * @return the commandSuccessful
		 */
		public boolean isCommandSuccessful() {
			return commandSuccessful;
		}

		public Response(Buffer buffer) {
			super(COMMAND_ID);
			
			commandSuccessful = buffer.get(0) != 0x00;
		}
		

		@Override
		public String toString() {
			return "RequestNodeInformation.Response [commandSuccessful=" + commandSuccessful + "]";
		}
	}
}

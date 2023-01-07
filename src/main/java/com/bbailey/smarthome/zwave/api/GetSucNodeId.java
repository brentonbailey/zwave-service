package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.4.1.6 Get SUC NodeID Command
 * This command is used to get currently registered SUC/SIS NodeID in a Z-Wave network.
 */
@CommandFrame(
		value = GetSucNodeId.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE,
		requestClass = GetSucNodeId.Request.class,
		responseClass = GetSucNodeId.Response.class
	)
public class GetSucNodeId {

	public static final int COMMAND_ID = 0x56;
	
	public static class Request extends SerializableCommand {
		 
		public Request() {
			super(COMMAND_ID);
		}
		
		@Override
		public Buffer serialize() {
			return Buffer.empty();
		}
	}
	
	
	public static class Response extends DeserializableCommand {
		
		private final int sucNodeId;
		
		/**
		 * @return the sucNodeId
		 */
		public int getSucNodeId() {
			return sucNodeId;
		}

		public Response(Buffer buffer) {
			super(COMMAND_ID);
			this.sucNodeId = buffer.next();
		}
		

		@Override
		public String toString() {
			return "GetSucNodeId.Response [sucNodeId=" + sucNodeId + "]";
		}
		
		
	}
}

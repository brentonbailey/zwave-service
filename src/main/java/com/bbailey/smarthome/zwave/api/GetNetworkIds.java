package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * 4.5.1 Get Network IDs from Memory Command
 * This command is used to get the HomeID and NodeID from the Z-Wave Module
 */
@CommandFrame(
		value = GetNetworkIds.COMMAND_ID, 
		requestClass = GetNetworkIds.Request.class, 
		responseClass = GetNetworkIds.Response.class, 
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE
	)
public class GetNetworkIds {

	public final static int COMMAND_ID = 0x20;
	
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

		private final int homeId;
		private final int nodeId;
		
		
		/**
		 * The current home Id of the Z-wave module
		 * @return the homeId
		 */
		public int getHomeId() {
			return homeId;
		}


		/**
		 * The current nodeId of the Z-wave module
		 * @return the nodeId
		 */
		public int getNodeId() {
			return nodeId;
		}


		public Response(Buffer buffer) {
			super(COMMAND_ID);
			
			this.homeId = BitUtils.convertToInt32(
					buffer.next(),
					buffer.next(),
					buffer.next(),
					buffer.next()
				);
			
			this.nodeId = buffer.next();
				
		}

		@Override
		public String toString() {
			return "GetNetworkIds.Response [homeId=" + homeId + ", nodeId=" + nodeId + "]";
		}		
	}
}

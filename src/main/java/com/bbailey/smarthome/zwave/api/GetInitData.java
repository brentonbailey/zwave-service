package com.bbailey.smarthome.zwave.api;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;


/**
 * 4.3.1 Get Init Data Command
 * This command is used to request the initialization data and current node list in the network. 
 */
@CommandFrame(
		value = GetInitData.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE,
		requestClass = GetInitData.Request.class,
		responseClass = GetInitData.Response.class
	)
public class GetInitData {

	public static final int COMMAND_ID = 0x02;
	
	public enum Capabilities {
		CONTROLLER(0x01),
		TIMER_FUNCTIONS(0x02),
		PRIMARY_CONTROLLER(0x04),
		SIS_FUNCTIONALITY(0x08);
		
		final int bitMask;
		
		private Capabilities(int bitMask) {
			this.bitMask = bitMask;
		}
		
		public boolean matches(int value) {
			return (value & bitMask) > 0;
		}
	}
	
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
		
		private final int apiVersion;
		private final EnumSet<Capabilities> apiCapabilities;
		private final List<Integer> nodes;
		private final int chipType;
		private final int chipVersion;
		
		/*
		 * Controller nodes must set the node list length to 29.
		 * 29 * 8 = gives us 232 bits to represent each node in
		 * the network
		 */
		private static final int CONTROLLER_NODE_LENGTH = 29;
		
		/**
		 * @return the apiVersion
		 */
		public int getApiVersion() {
			return apiVersion;
		}
		
		
		/**
		 * @return the apiCapabilities
		 */
		public EnumSet<Capabilities> getApiCapabilities() {
			return apiCapabilities;
		}
		
		
		/**
		 * @return the nodes
		 */
		public List<Integer> getNodes() {
			return nodes;
		}
		
		
		/**
		 * @return the chipType
		 */
		public int getChipType() {
			return chipType;
		}
		
		
		/**
		 * @return the chipVersion
		 */
		public int getChipVersion() {
			return chipVersion;
		}
		
		
		public Response(Buffer buffer) {
			
			super(COMMAND_ID);

			this.apiVersion = buffer.next();
			
			int capabilities = buffer.next();
			this.apiCapabilities = EnumSet.noneOf(Capabilities.class);
			for (Capabilities capability : Capabilities.values()) {
				if (capability.matches(capabilities)) {
					this.apiCapabilities.add(capability);
				}
			}
			
			int nodeLength = buffer.next();
			if (nodeLength != CONTROLLER_NODE_LENGTH) {
				throw new IllegalArgumentException("Controller must return node length of " + CONTROLLER_NODE_LENGTH + ", received " + nodeLength);
			}
			
			this.nodes = new ArrayList<>();
			int nodeId = 1;
			for (int i = 0 ; i < nodeLength ; i++) {
				
				int nodeByte = buffer.next();
				for (int b = 0 ; b < 8 ; b++) {
					// Check each bit in the byte
					if (BitUtils.isBitN(nodeByte, b)) {
						this.nodes.add(nodeId++);
					}
				}
			}

			this.chipType = buffer.next();
			this.chipVersion = buffer.next();
		}
		

		@Override
		public String toString() {
			return "GetInitData.Response [apiVersion=" + apiVersion + ", "
					+ (apiCapabilities != null ? "apiCapabilities=" + apiCapabilities + ", " : "")
					+ (nodes != null ? "nodes=" + nodes + ", " : "") + "chipType=" + chipType + ", chipVersion="
					+ chipVersion + "]";
		}
	}
}

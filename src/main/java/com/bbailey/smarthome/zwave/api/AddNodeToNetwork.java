package com.bbailey.smarthome.zwave.api;

import java.util.ArrayList;
import java.util.List;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.device.BasicDeviceType;
import com.bbailey.smarthome.zwave.device.GenericDeviceType;
import com.bbailey.smarthome.zwave.device.SpecificDeviceType;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * 4.4.3.1 Add Node To Network Command
 * 
 * This command is used to trigger a node inclusion to a Z-Wave network. The Add Node To Network Command Identifier is 0x4A.
 * This Command MUST be supported by Controller Nodes Z-Wave API implementations. This Command MUST NOT be supported by End Nodes Z-Wave API implementations.
 */
@CommandFrame(
		value = AddNodeToNetwork.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_CALLBACK,
		requestClass = AddNodeToNetwork.Request.class,
		callbackClass = AddNodeToNetwork.Callback.class
	)
public class AddNodeToNetwork {

	public static final int COMMAND_ID = 0x4A;
	
	public static class Request extends SerializableCommand {
	
		public enum Mode {
			ANY(0x01),
			STOP_INCLUSION(0x05),
			STOP_CONTROLLER_REPLICATION(0x06),
			SMART_START_INCLUD_NODE(0x08),
			START_SMART_START(0x09);
			
			private final int protocolValue;
			
			/**
			 * @return the protocolValue
			 */
			public int toProtocolValue() {
				return protocolValue;
			}

			private Mode(int protocolValue) {
				this.protocolValue = protocolValue;
			}
			
			public static Mode fromProtocolValue(int protocolValue) {
				for (Mode mode : Mode.values()) {
					if (mode.protocolValue == protocolValue) {
						return mode;
					}
				}
				
				throw new IllegalArgumentException("Unknown inclusion mode: " + BitUtils.toHex(protocolValue));
			}
		}
		
		private final boolean normalPower;
		private final boolean networkWideInclusion;
		private final Mode mode;
		private final int sessionId;
		
		
		public Request(boolean normalPower, boolean networkWideInclusion, Mode mode, int sessionId) {
			super(COMMAND_ID);
			this.normalPower = normalPower;
			this.networkWideInclusion = networkWideInclusion;
			this.mode = mode;
			this.sessionId = sessionId;
		}
		
		
		@Override
		public Buffer serialize() {
			Buffer buffer = new Buffer(10);
			
			int b1 = 0x00;
			if (normalPower) {
				b1 |= BitUtils.BIT_7;
			}
			
			if (networkWideInclusion) {
				b1 |= BitUtils.BIT_6;
			}
			
			b1 |= mode.toProtocolValue();
			
			buffer.set(0, b1);
			buffer.set(1, sessionId);
			
			// The remaining 8 bytes should be 0x00 if mode != 0x08
			
			return buffer;
		}


		@Override
		public String toString() {
			return "AddNodeToNetwork.Request [normalPower=" + normalPower + ", networkWideInclusion=" + networkWideInclusion + ", "
					+ (mode != null ? "mode=" + mode + ", " : "") + "sessionId=" + sessionId + "]";
		}
		
		
	}
	
	
	public static class Callback extends DeserializableCommand {
		
		public enum Status {
			/**
			 * The Z-Wave Module has initiated Network inclusion and is ready to include new nodes
			 */
			NETWORK_INCLUSION_STARTED(0x01),
			/**
			 * A node requesting inclusion has been found and the network inclusion is initiated.
			 */
			NODE_FOUND(0x02),
			/**
			 * The network inclusion is ongoing with an End Node.
			 */
			INCLUSION_ONGOING_WITH_END_NODE(0x03),
			/**
			 * The network inclusion is ongoing with a Controller node.
			 */
			INCLUSION_ONGOING_WITH_CONTROLLER_NODE(0x04),
			/**
			 * The network inclusion is completed. 
			 * The host application SHOULD issue a new Add Node To Network Command - Initial data frame with the 
			 * Mode field set to 0x05 to stop the network inclusion.
			 */
			INCLUSION_COMPLETED_PROTOCOL_PART(0x05),
			/**
			 * The network inclusion is completed. 
			 * The Z-Wave Module is ready to return to idle and the host application SHOULD issue a new 
			 * Add Node To Network Command - Initial data frame with the Mode field set to 0x05 to stop the network inclusion.
			 */
			INCLUSION_COMPLETED(0x06);
			
			private final int protocolValue;
			
			private Status(int protocolValue) {
				this.protocolValue = protocolValue;
			}
			
			public static Status fromProtocolValue(int protocolValue) {
				for (Status status : Status.values()) {
					if (status.protocolValue == protocolValue) {
						return status;
					}
				}
				
				throw new IllegalArgumentException("Unknown network inclusion status: " + BitUtils.toHex(protocolValue));
			}
			
		}
		
		private final int sessionId;
		private final Status status;
		private final int assignedNodeId;
		private final List<Integer> commandClasses;
		private final BasicDeviceType basicDeviceType;
		private final GenericDeviceType genericDeviceType;
		private final SpecificDeviceType specificDeviceType;
		
		

		/**
		 * @return the sessionId
		 */
		public int getSessionId() {
			return sessionId;
		}

		/**
		 * @return the status
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * @return the assignedNodeId
		 */
		public int getAssignedNodeId() {
			return assignedNodeId;
		}

		/**
		 * @return the commandClasses
		 */
		public List<Integer> getCommandClasses() {
			return commandClasses;
		}

		/**
		 * @return the basicDeviceType
		 */
		public BasicDeviceType getBasicDeviceType() {
			return basicDeviceType;
		}

		/**
		 * @return the genericDeviceType
		 */
		public GenericDeviceType getGenericDeviceType() {
			return genericDeviceType;
		}

		/**
		 * @return the specificDeviceType
		 */
		public SpecificDeviceType getSpecificDeviceType() {
			return specificDeviceType;
		}

		public Callback(Buffer buffer) {
			super(COMMAND_ID);
			this.sessionId = buffer.next();
			this.status = Status.fromProtocolValue(buffer.next());
			this.assignedNodeId = buffer.next();
			
			int commandLength = buffer.next();
			this.commandClasses = new ArrayList<>(commandLength);
			
			this.basicDeviceType = BasicDeviceType.fromProtocolValue(buffer.next());
			this.genericDeviceType = GenericDeviceType.fromProtocolVersion(buffer.next());
			this.specificDeviceType = SpecificDeviceType.fromProtocolVersion(genericDeviceType, buffer.next());
			
			for (int i = 0 ; i < commandLength ; i++) {
				commandClasses.add(i, buffer.next());
			}
		}
		

		@Override
		public String toString() {
			return "AddNodeToNetwork.Callback [sessionId=" + sessionId + ", " + (status != null ? "status=" + status + ", " : "")
					+ "assignedNodeId=" + assignedNodeId + ", "
					+ (commandClasses != null ? "commandClasses=" + commandClasses + ", " : "")
					+ (basicDeviceType != null ? "basicDeviceType=" + basicDeviceType + ", " : "")
					+ (genericDeviceType != null ? "genericDeviceType=" + genericDeviceType + ", " : "")
					+ (specificDeviceType != null ? "specificDeviceType=" + specificDeviceType : "") + "]";
		}
		
		
	}
}

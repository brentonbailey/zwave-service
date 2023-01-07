package com.bbailey.smarthome.zwave.api;

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
 * 4.4.1.2 GetNodeInformationProtocolDataCommand
 * 
 * This command is used to request the Node Information protocol data about a NodeID to the Z-Wave API Module. 
 */
@CommandFrame(
		value = GetNodeInformationProtocolData.COMMAND_ID, 
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE, 
		requestClass = GetNodeInformationProtocolData.Request.class, 
		responseClass = GetNodeInformationProtocolData.Response.class
	)
public class GetNodeInformationProtocolData {
	
	public final static int COMMAND_ID = 0x41;
	
	public static class Request extends SerializableCommand {
		
		private final int nodeId;
		
		public Request(int nodeId) {
			super(COMMAND_ID);
			this.nodeId = nodeId;
		}
		
		@Override
		public Buffer serialize() {
			return Buffer.of(this.nodeId);
		}

		@Override
		public String toString() {
			return "GetNodeInformationProtocolData.Request [nodeId=" + BitUtils.toHex(nodeId) + "]";
		}
	}

	
	public static class Response extends DeserializableCommand {
	
		private final boolean listening;
		private final boolean routing;
		private final int supportedSpeed;
		private final int protocolVersion;
		private final boolean optionalFunctionality;
		private final boolean sensor1000ms;
		private final boolean sensor250ms;
		private final boolean beamCapability;
		private final boolean routingEndNode;
		private final boolean specificDevice;
		private final boolean controllerNode;
		private final boolean security;
		private final int speedExtension;
		private final BasicDeviceType basicDeviceType;
		private final GenericDeviceType genericDeviceClass;
		private final SpecificDeviceType specificDeviceClass;
		
		/**
		 * @return the listening
		 */
		public boolean isListening() {
			return listening;
		}
	
		/**
		 * @return the routing
		 */
		public boolean isRouting() {
			return routing;
		}
	
		/**
		 * @return the supportedSpeed
		 */
		public int getSupportedSpeed() {
			return supportedSpeed;
		}
	
		/**
		 * @return the protocolVersion
		 */
		public int getProtocolVersion() {
			return protocolVersion;
		}
	
		/**
		 * @return the optionalFunctionality
		 */
		public boolean isOptionalFunctionality() {
			return optionalFunctionality;
		}
	
		/**
		 * @return the sensor1000ms
		 */
		public boolean isSensor1000ms() {
			return sensor1000ms;
		}
	
		/**
		 * @return the sensor250ms
		 */
		public boolean isSensor250ms() {
			return sensor250ms;
		}
	
		/**
		 * @return the beamCapability
		 */
		public boolean isBeamCapability() {
			return beamCapability;
		}
	
		/**
		 * @return the routingEndNode
		 */
		public boolean isRoutingEndNode() {
			return routingEndNode;
		}
	
		/**
		 * @return the specificDevice
		 */
		public boolean isSpecificDevice() {
			return specificDevice;
		}
	
		/**
		 * @return the controllerNode
		 */
		public boolean isControllerNode() {
			return controllerNode;
		}
	
		/**
		 * @return the security
		 */
		public boolean isSecurity() {
			return security;
		}
	
		/**
		 * @return the speedExtension
		 */
		public int getSpeedExtension() {
			return speedExtension;
		}
	
		/**
		 * @return the basicDeviceType
		 */
		public BasicDeviceType getBasicDeviceType() {
			return basicDeviceType;
		}
	
		/**
		 * @return the genericDeviceClass
		 */
		public GenericDeviceType getGenericDeviceClass() {
			return genericDeviceClass;
		}
	
		/**
		 * @return the specificDeviceClass
		 */
		public SpecificDeviceType getSpecificDeviceClass() {
			return specificDeviceClass;
		}
	

		public Response(Buffer buffer) {
			super(COMMAND_ID);
			
			
			// Decode the first byte
			int b1 = buffer.next();
			listening = BitUtils.isBit7(b1);
			routing = BitUtils.isBit6(b1);
			
			// Basic speed is 9600 baud or 40000 if speed = 4
			int baudRate = 9600;
			if ((b1 & 0x38) == 0x10) {
				baudRate = 40000;
			}
			supportedSpeed = baudRate;
			
			// Protocol version is 0 based, need to add 1
			protocolVersion = (b1 & 0x07) + 1;
			
			// Decode the second byte
			int b2 = buffer.next();
			optionalFunctionality = BitUtils.isBit7(b2);
			
			// If either of these are set, the node is frequently listening
			sensor1000ms = BitUtils.isBit6(b2);
			sensor250ms = BitUtils.isBit5(b2);
			beamCapability = BitUtils.isBit4(b2);
			routingEndNode = BitUtils.isBit3(b2);
			specificDevice = BitUtils.isBit2(b2);
			controllerNode = BitUtils.isBit1(b2);
			security = BitUtils.isBit0(b2);
			
			// Decode the third byte
			speedExtension = buffer.next() & 0x07;
			basicDeviceType = BasicDeviceType.fromProtocolValue(buffer.next());
			genericDeviceClass = GenericDeviceType.fromProtocolVersion(buffer.next());
			specificDeviceClass = SpecificDeviceType.fromProtocolVersion(genericDeviceClass, buffer.next());
		}
	


		@Override
		public String toString() {
			return "GetNodeInformationProtocolData.Response [listening=" + listening + ", routing=" + routing + ", supportedSpeed=" + supportedSpeed
					+ ", protocolVersion=" + protocolVersion + ", optionalFunctionality=" + optionalFunctionality
					+ ", sensor1000ms=" + sensor1000ms + ", sensor250ms=" + sensor250ms + ", beamCapability="
					+ beamCapability + ", routingEndNode=" + routingEndNode + ", specificDevice=" + specificDevice
					+ ", controllerNode=" + controllerNode + ", security=" + security + ", speedExtension="
					+ speedExtension + ", basicDeviceType=" + basicDeviceType + ", genericDeviceClass="
					+ genericDeviceClass + ", specificDeviceClass=" + specificDeviceClass + "]";
		}

	}
}

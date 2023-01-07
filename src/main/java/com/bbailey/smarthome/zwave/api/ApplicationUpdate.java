package com.bbailey.smarthome.zwave.api;

import java.util.ArrayList;
import java.util.List;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.device.BasicDeviceType;
import com.bbailey.smarthome.zwave.device.GenericDeviceType;
import com.bbailey.smarthome.zwave.device.SpecificDeviceType;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

@CommandFrame(
		value = ApplicationUpdate.COMMAND_ID,
		flow = CommandFlow.UNSOLICITED,
		requestClass = ApplicationUpdate.Request.class
	)
public class ApplicationUpdate {

	public final static int COMMAND_ID = 0x49;
	
	public enum EventType {
		/**
		 * The SIS NodeID has been updated
		 */
		UPDATE_STATE_SUC_ID(0x10),
		/**
		 * A node has been deleted from the network
		 */
		UPDATE_STATE_DELETE_DONE(0x20),
		/**
		 * A new node as been added to the network
		 */
		UPDATE_STATE_NEW_ID_ASSIGNED(0x40),
		/**
		 * Another node in the network as requested the Z-Wave API module to perform a neighbor discovery
		 */
		UPDATE_STATE_ROUTING_PENDING(0x80),
		/**
		 * The issued Request Node Information Command has not been acknowledged by the destination
		 */
		UPDATE_STATE_NODE_INFO_REQ_FAILED(0x81),
		/**
		 * The issued Request Node Information Command has been acknowledged by the destination.
		 */
		UPDATE_STATE_NODE_INFO_REQ_DONE(0x82),
		/**
		 * Another node sent a NOP Power Command to the Z-Wave API Module. The host application SHOULD NOT power down the Z-Wave API Module.
		 */
		UPDATE_STATE_NOP_POWER_RECEIVED(0x83),
		/**
		 * A Node Information Frame has been received as unsolicited frame or in response to a Request Node Information Command.
		 */
		UPDATE_STATE_NODE_INFO_RECEIVED(0x84),
		/**
		 * A SmartStart Prime Command has been received using the Z-Wave protocol.
		 */
		UPDATE_STATE_NODE_INFO_SMARTSTART_HOMEID_RECEIVED(0x85),
		/**
		 * A SmartStart Included Node Information Frame has been received (using either Z-Wave or Z-Wave Long Range protocol).
		 */
		UPDATE_STATE_INCLUDED_NODE_INFO_RECEIVED(0x86),
		/**
		 * A SmartStart Prime Command has been received using the Z-Wave Long Range protocol.
		 */
		UPDATE_STATE_NODE_INFO_SMARTSTART_HOMEID_RECEIVED_LR(0x87);
		
		private final int protocolVersion;
		
		private EventType(int protocolVersion) {
			this.protocolVersion = protocolVersion;
		}
		
		public static EventType fromProtocolVersion(int protocolVersion) {
			for (EventType eventType : EventType.values()) {
				if (eventType.protocolVersion == protocolVersion) {
					return eventType;
				}
			}
			
			throw new IllegalArgumentException("Unsupported event type: " + BitUtils.toHex(protocolVersion));
		}
	}
	
	public static class Request extends DeserializableCommand {
		
		private final EventType event;
		private final int remoteNodeId;
		private BasicDeviceType basicDeviceClass;
		private GenericDeviceType genericDeviceType;
		private SpecificDeviceType specificDeviceType;
		private List<Integer> supportedCommands;
		
		/**
		 * @return the basicDeviceClass
		 */
		public BasicDeviceType getBasicDeviceClass() {
			return basicDeviceClass;
		}

		/**
		 * @param basicDeviceClass the basicDeviceClass to set
		 */
		public void setBasicDeviceClass(BasicDeviceType basicDeviceClass) {
			this.basicDeviceClass = basicDeviceClass;
		}

		/**
		 * @return the genericDeviceType
		 */
		public GenericDeviceType getGenericDeviceType() {
			return genericDeviceType;
		}

		/**
		 * @param genericDeviceType the genericDeviceType to set
		 */
		public void setGenericDeviceType(GenericDeviceType genericDeviceType) {
			this.genericDeviceType = genericDeviceType;
		}

		/**
		 * @return the specificDeviceType
		 */
		public SpecificDeviceType getSpecificDeviceType() {
			return specificDeviceType;
		}

		/**
		 * @param specificDeviceType the specificDeviceType to set
		 */
		public void setSpecificDeviceType(SpecificDeviceType specificDeviceType) {
			this.specificDeviceType = specificDeviceType;
		}

		/**
		 * @return the supportedCommands
		 */
		public List<Integer> getSupportedCommands() {
			return supportedCommands;
		}

		/**
		 * @param supportedCommands the supportedCommands to set
		 */
		public void setSupportedCommands(List<Integer> supportedCommands) {
			this.supportedCommands = supportedCommands;
		}

		/**
		 * @return the event
		 */
		public EventType getEvent() {
			return event;
		}

		/**
		 * @return the remoteNodeId
		 */
		public int getRemoteNodeId() {
			return remoteNodeId;
		}

		public Request(Buffer buffer) {
			super(COMMAND_ID);
			
			this.event = EventType.fromProtocolVersion(buffer.next());
			this.remoteNodeId = buffer.next();
			
			// We subtract 3 here because the length appears to also include the 3 device class fields
			int length = buffer.next();
			
			if (length > 0) {
				/*
				 * For modules that fail to respond with a NIF (e.g. controllers)
				 * the remoteNodeId will be 0 and not device information will be given
				 */
				this.basicDeviceClass = BasicDeviceType.fromProtocolValue(buffer.next());
				this.genericDeviceType = GenericDeviceType.fromProtocolVersion(buffer.next());
				this.specificDeviceType = SpecificDeviceType.fromProtocolVersion(genericDeviceType, buffer.next());
			
				this.supportedCommands = new ArrayList<>(length - 3);
				for (int i = 0 ; i < length - 3 ; i++) {
					this.supportedCommands.add(i, buffer.next());
				}
			}
		}

		@Override
		public String toString() {
			return "ApplicationUpdate.Request [" + (event != null ? "event=" + event + ", " : "") + "remoteNodeId=" + remoteNodeId + ", "
					+ (basicDeviceClass != null ? "basicDeviceClass=" + basicDeviceClass + ", " : "")
					+ (genericDeviceType != null ? "genericDeviceType=" + genericDeviceType + ", " : "")
					+ (specificDeviceType != null ? "specificDeviceType=" + specificDeviceType + ", " : "")
					+ (supportedCommands != null ? "supportedCommands=" + supportedCommands : "") + "]";
		}
	}
}

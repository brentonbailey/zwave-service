package com.bbailey.smarthome.zwave.api;

import java.util.EnumSet;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.3.4 GetControllerCapabilitiesCommand
 * This command is used to request a controller from its current network capabilities.
 */
@CommandFrame(
		value = GetControllerCapabilities.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE,
		requestClass = GetControllerCapabilities.Request.class,
		responseClass =  GetControllerCapabilities.Response.class
	)
public class GetControllerCapabilities {

	public static final int COMMAND_ID = 0x05;
	
	public enum ControllerCapabilities {
		SECONDARY_CONTROLLER(0xF0),
		OTHER_NETWORK(0x80),
		SIS_IS_PRESENT(0x40),
		SUC_ENABLED(0x10),
		NO_NODES_INCLUDED(0x0F);

		final int bitMask;
		
		private ControllerCapabilities(int bitMask) {
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
		
		
		private final EnumSet<ControllerCapabilities> capabilities = EnumSet.noneOf(ControllerCapabilities.class);
		
		/**
		 * @return the capabilities
		 */
		public EnumSet<ControllerCapabilities> getCapabilities() {
			return capabilities;
		}

		public Response(Buffer buffer) {
			super(COMMAND_ID);
			
			for (ControllerCapabilities capability : ControllerCapabilities.values()) {
				if (capability.matches(buffer.get(0))) {
					capabilities.add(capability);
				}
			}
		}
		

		@Override
		public String toString() {
			return "GetControllerCapabilities.Response [" + (capabilities != null ? "capabilities=" + capabilities : "") + "]";
		}
	}
}

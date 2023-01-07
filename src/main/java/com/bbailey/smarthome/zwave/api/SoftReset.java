package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.3.12 Soft Reset Command
 * This command is used to request the Z-Wave Module to perform a soft reset.
 */
@CommandFrame(
		value = SoftReset.COMMAND_ID, 
		flow = CommandFlow.ACK_FRAME, 
		requestClass = SoftReset.Request.class
	)
public class SoftReset {

	public final static int COMMAND_ID = 0x08;
	
	public static class Request extends SerializableCommand {
		
		public Request() {
			super(COMMAND_ID);
		}
		
		@Override
		public Buffer serialize() {
			return Buffer.empty();
		}
	}
}

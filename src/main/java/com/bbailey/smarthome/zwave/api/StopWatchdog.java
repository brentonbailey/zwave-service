package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.8.13 StopWatchdogCommand
 * This command is used to stop Watchdog functionality on Z-Wave module.
 */
@CommandFrame(
		value = StopWatchdog.COMMAND_ID,
		flow = CommandFlow.UNACK_FRAME,
		requestClass = StopWatchdog.Request.class
	)
public class StopWatchdog {

	public static final int COMMAND_ID = 0xD3;
	
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

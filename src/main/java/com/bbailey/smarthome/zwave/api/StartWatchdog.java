package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.8.12 StartWatchdogCommand
 * This command is used to start Watchdog functionality on Z-Wave module.
 */
@CommandFrame(
		value = StartWatchdog.COMMAND_ID,
		flow = CommandFlow.UNACK_FRAME,
		requestClass = StartWatchdog.Request.class
	)
public class StartWatchdog {

	public static final int COMMAND_ID = 0xD2;
	
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

package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.9.7 Send Data Abort Command
 * This command is used to instruct the Z-Wave Module to abort an ongoing transmission started with any of the following commands:
 * • BridgeControllerNodeSendDataCommand
 * • BridgeControllerNodeSendDataMulticastCommand • End Node Send Data Command
 * • EndNodeSendDataMulticastCommand
 * • ControllerNodeSendDataCommand
 * • ControllerNodeSendDataMulticastCommand
 * • SendNOPCommand
 */
@CommandFrame(
		value = SendDataAbort.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME,
		requestClass = SendDataAbort.Request.class
	)
public class SendDataAbort {

	public final static int COMMAND_ID = 0x16;
	
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

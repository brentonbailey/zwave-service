package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.RssiMeasurement;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.8.2 GetBackgroundRSSICommand
 * 
 * This command is used to request the most recent background RSSI levels detected.
 * NOTE: The RSSI shall only be measured when the radio is in receive mode.
 */
@CommandFrame(
		value = GetBackgroundRssi.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE,
		requestClass = GetBackgroundRssi.Request.class,
		responseClass = GetBackgroundRssi.Response.class
	)
public class GetBackgroundRssi {

	public final static int COMMAND_ID = 0x3B;
	
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
		
		private final RssiMeasurement channel0;
		private final RssiMeasurement channel1;
		private final RssiMeasurement channel2;
		
		public Response(Buffer buffer) {
			super(COMMAND_ID);
			this.channel0 = new RssiMeasurement(buffer.next());
			this.channel1 = new RssiMeasurement(buffer.next());
			this.channel2 = new RssiMeasurement(buffer.next());
		}

		@Override
		public String toString() {
			return "GetBackgroundRssi.Response [" + (channel0 != null ? "channel0=" + channel0 + ", " : "")
					+ (channel1 != null ? "channel1=" + channel1 + ", " : "")
					+ (channel2 != null ? "channel2=" + channel2 : "") + "]";
		}
	}
}

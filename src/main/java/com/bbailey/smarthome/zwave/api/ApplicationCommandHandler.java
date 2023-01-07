package com.bbailey.smarthome.zwave.api;

import java.util.EnumSet;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.RssiMeasurement;
import com.bbailey.smarthome.zwave.api.common.RxStatus;
import com.bbailey.smarthome.zwave.protocol.Buffer;


/**
 * 4.7.1 ApplicationCommandHandlerCommand
 * 
 * This command is used by a Z-Wave module to notify a host application that a Z-Wave frame has been received.
 */
@CommandFrame(
		value = ApplicationCommandHandler.COMMAND_ID,
		flow = CommandFlow.UNSOLICITED,
		requestClass = ApplicationCommandHandler.Request.class
	)
public class ApplicationCommandHandler {

	public final static int COMMAND_ID = 0x04;
	
	public static class Request extends DeserializableCommand {
		
		private final EnumSet<RxStatus> rxStatus;
		private final int sourceNodeId;
		private final Buffer payload;
		private final RssiMeasurement rssi;
		
		/**
		 * @return the rxStatus
		 */
		public EnumSet<RxStatus> getRxStatus() {
			return rxStatus;
		}

		/**
		 * @return the sourceNodeId
		 */
		public int getSourceNodeId() {
			return sourceNodeId;
		}

		/**
		 * @return the payload
		 */
		public Buffer getPayload() {
			return payload;
		}

		/**
		 * @return the rssi
		 */
		public RssiMeasurement getRssi() {
			return rssi;
		}

		public Request(Buffer buffer) {
			super(COMMAND_ID);
			
			int b = buffer.next();
			this.rxStatus = EnumSet.noneOf(RxStatus.class);
			for (RxStatus rxStatus : RxStatus.values()) {
				if (rxStatus.matches(b)) {
					this.rxStatus.add(rxStatus);
				}
			}
			
			this.sourceNodeId = buffer.next();
			
			int payloadLength = buffer.next();
			this.payload = new Buffer(payloadLength);
			for (int i = 0 ; i < payloadLength ; i++) {
				payload.set(i,  buffer.next());
			}
			
			this.rssi = new RssiMeasurement(buffer.next());
		}
		
		
		@Override
		public String toString() {
			return "ApplicationCommandHandler.Request [" + (rxStatus != null ? "rxStatus=" + rxStatus + ", " : "") + "sourceNodeId="
					+ sourceNodeId + ", " + (payload != null ? "payload=" + payload + ", " : "")
					+ (rssi != null ? "rssi=" + rssi : "") + "]";
		}
		
		
	}
}

package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.8.14 SetTimeoutsCommand
 * This command is used to set timeouts with 10ms ticks.
 */
public class SetTimeouts {

	public static final int COMMAND_ID = 0x06;
	
	public static class Request extends SerializableCommand {
		
		private final int rxAckTimeout;
		private final int rxByteTimeout;
		
		/**
		 * This field is used to indicate the maximum time to wait for ACK after frame transmission, in 10ms ticks
		 * @return the rxAckTimeout
		 */
		public int getRxAckTimeout() {
			return rxAckTimeout;
		}

		/**
		 * This field is used to indicate the maximum time to wait for next byte when receiving a new frame, in 10ms ticks.
		 * @return the rxByteTimeout
		 */
		public int getRxByteTimeout() {
			return rxByteTimeout;
		}

		public Request(int rxAckTimeout, int rxByteTimeout) {
			super(COMMAND_ID);
			this.rxAckTimeout = rxAckTimeout;
			this.rxByteTimeout = rxByteTimeout;
		}
		
		@Override
		public Buffer serialize() {
			return Buffer.of(rxAckTimeout, rxByteTimeout);
		}

		@Override
		public String toString() {
			return "SetTimeouts.Request [rxAckTimeout=" + rxAckTimeout + ", rxByteTimeout=" + rxByteTimeout + "]";
		}
		
		
	}
	
	public static class Response extends DeserializableCommand {
		
		private final int previousRxAckTimeout;
		private final int previousRxByteTimeout;
		
		
		/**
		 * This field is used to indicate previous Rx ACK timeout setting, in 10ms ticks.
		 * @return the previousRxAckTimeout
		 */
		public int getPreviousRxAckTimeout() {
			return previousRxAckTimeout;
		}
		
		
		/**
		 * This field is used to indicate previous Rx BYTE timeout setting, in 10ms ticks
		 * @return the previousRxByteTimeout
		 */
		public int getPreviousRxByteTimeout() {
			return previousRxByteTimeout;
		}
		
		public Response(Buffer buffer) {
			super(COMMAND_ID);
			this.previousRxAckTimeout = buffer.next();
			this.previousRxByteTimeout = buffer.next();
		}
		

		@Override
		public String toString() {
			return "SetTimeout.Response [previousRxAckTimeout=" + previousRxAckTimeout + ", previousRxByteTimeout="
					+ previousRxByteTimeout + "]";
		}
		
		
	}
}

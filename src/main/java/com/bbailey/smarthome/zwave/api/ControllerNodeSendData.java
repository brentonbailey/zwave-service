package com.bbailey.smarthome.zwave.api;

import java.util.Arrays;
import java.util.EnumSet;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.api.common.TxOptions;
import com.bbailey.smarthome.zwave.api.common.TxStatus;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.9.1 Controller Node Send Data Command
 * 
 * This command is used to transmit contents of a data buffer to a single node or all nodes (broadcast).
 */
@CommandFrame(
		value = ControllerNodeSendData.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE_AND_CALLBACK,
		requestClass = ControllerNodeSendData.Request.class,
		responseClass = ControllerNodeSendData.Response.class,
		callbackClass = ControllerNodeSendData.Callback.class
	)
public class ControllerNodeSendData {

	public final static int COMMAND_ID = 0x13;
	
	public static class Request extends SerializableCommand {

		private final int destinationNodeId;
		private final Buffer data;
		private final EnumSet<TxOptions> transmitOptions;
		private final int sessionId;
		
		
		
		/**
		 * @return the destinationNodeId
		 */
		public int getDestinationNodeId() {
			return destinationNodeId;
		}

		/**
		 * @return the data
		 */
		public Buffer getData() {
			return data;
		}

		/**
		 * @return the transmitOptions
		 */
		public EnumSet<TxOptions> getTransmitOptions() {
			return transmitOptions;
		}

		/**
		 * @return the sessionId
		 */
		public int getSessionId() {
			return sessionId;
		}

		public Request(int destinationNodeId, Buffer data, EnumSet<TxOptions> transmitOptions, int sessionId) {
			super(COMMAND_ID);
			this.destinationNodeId = destinationNodeId;
			this.data = data;
			this.transmitOptions = transmitOptions;
			this.sessionId = sessionId;
		}
		
		@Override
		public Buffer serialize() {
			Buffer buffer = new Buffer(4 + data.length());
			buffer.set(0,  destinationNodeId);
			buffer.set(1,  data.length());
			for (int i = 0 ; i < data.length() ; i++) {
				buffer.set(i + 2, data.get(i));
			}
			
			int b1 = 0x00;
			for (TxOptions option : transmitOptions) {
				b1 |= option.getBitMask();
			}
			
			buffer.set(data.length() + 2, b1);
			buffer.set(data.length() + 3, sessionId);
			
			return buffer;
		}

		@Override
		public String toString() {
			return "ControllerNodeSendData.Request [destinationNodeId=" + destinationNodeId + ", "
					+ (data != null ? "data=" + data + ", " : "")
					+ (transmitOptions != null ? "transmitOptions=" + transmitOptions + ", " : "") + "sessionId="
					+ sessionId + "]";
		}
	}
	
	
	public static class Response extends DeserializableCommand {
		
		private final boolean commandAccepted;
		
		/**
		 * If the command was accepted, we can expect a callback
		 * @return the commandAccepted
		 */
		public boolean isCommandAccepted() {
			return commandAccepted;
		}


		public Response(Buffer buffer) {
			super(COMMAND_ID);
			commandAccepted = buffer.next() != 0x00;
		}

		@Override
		public String toString() {
			return "ControllerNodeSendData.Response [commandAccepted=" + commandAccepted + "]";
		}
	}
	
	
	public static class Callback extends DeserializableCommand {
		
		private final int sessionId;
		private final TxStatus txStatus;
		private final int[] txStatusReport;
		
		
		/**
		 * @return the sessionId
		 */
		public int getSessionId() {
			return sessionId;
		}

		/**
		 * @return the txStatus
		 */
		public TxStatus getTxStatus() {
			return txStatus;
		}

		/**
		 * @return the txStatusReport
		 */
		public int[] getTxStatusReport() {
			return txStatusReport;
		}


		public Callback(Buffer buffer) {
			super(COMMAND_ID);
			this.sessionId = buffer.next();
			this.txStatus = buffer.hasNext() ? TxStatus.fromProtocolValue(buffer.next()) : TxStatus.TRANSMIT_NO_REPORT;
			
			int reportLength = buffer.length() - 2;
			if (reportLength < 0) {
				// There is no report provided
				reportLength = 0;
			}
			
			this.txStatusReport = new int[reportLength];
			for (int i = 0 ; i < reportLength ; i++) {
				txStatusReport[i] = buffer.next();
			}
			
		}


		@Override
		public String toString() {
			return "ControllerNodeSendData.Callback [sessionId=" + sessionId + ", " + (txStatus != null ? "txStatus=" + txStatus + ", " : "")
					+ (txStatusReport != null ? "txStatusReport=" + Arrays.toString(txStatusReport) : "") + "]";
		}
	}
}

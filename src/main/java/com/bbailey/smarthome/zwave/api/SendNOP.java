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
 * 4.4.1.1 SendNOPCommand
 *
 * This command is used to send NOP Commands a destination to verify if it is responsive. 
 * This command SHOULD NOT be used by a host application for NL Nodes outside their Wake Up period.
 */
@CommandFrame(
		value = SendNOP.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE_AND_CALLBACK,
		requestClass = SendNOP.Request.class,
		responseClass = SendNOP.Response.class,
		callbackClass = SendNOP.Callback.class
	)
public class SendNOP {

	public final static int COMMAND_ID = 0xE9;
	
	public static class Request extends SerializableCommand {
		
		private final int destinationNodeId;
		private EnumSet<TxOptions> txOptions;
		private final int sessionId;
		
		/**
		 * @return the txOptions
		 */
		public EnumSet<TxOptions> getTxOptions() {
			return txOptions;
		}

		/**
		 * @param txOptions the txOptions to set
		 */
		public void setTxOptions(EnumSet<TxOptions> txOptions) {
			this.txOptions = txOptions;
		}

		/**
		 * @return the destinationNodeId
		 */
		public int getDestinationNodeId() {
			return destinationNodeId;
		}

		/**
		 * @return the sessionId
		 */
		public int getSessionId() {
			return sessionId;
		}


		public Request(int destinationNodeId, EnumSet<TxOptions> txOptions, int sessionId) {
			super(COMMAND_ID);
			this.destinationNodeId = destinationNodeId;
			this.txOptions = txOptions;
			this.sessionId = sessionId;
		}
		
		@Override
		public Buffer serialize() {
			
			int tx = 0x01;
			for (TxOptions option : txOptions) {
				tx |= option.getBitMask();
			}
			
			return Buffer.of(destinationNodeId, tx, sessionId);
		}

		@Override
		public String toString() {
			return "SendNOP.Request [destinationNodeId=" + destinationNodeId + ", "
					+ (txOptions != null ? "txOptions=" + txOptions + ", " : "") + "sessionId=" + sessionId + "]";
		}
	}
	
	
	public static class Response extends DeserializableCommand {
		
		private final boolean commandSuccess;

		/**
		 * @return the commandSuccess
		 */
		public boolean isCommandSuccess() {
			return commandSuccess;
		}
		
		public Response(Buffer buffer) {
			super(COMMAND_ID);
			this.commandSuccess = buffer.next() != 0x00;
		}


		@Override
		public String toString() {
			return "SendNOP.Response [commandSuccess=" + commandSuccess + "]";
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
			this.txStatus = TxStatus.fromProtocolValue(buffer.next());
			
			int length = buffer.length() - 2;
			this.txStatusReport = new int[length];
			for (int i = 0 ; i < length ; i++) {
				this.txStatusReport[i] = buffer.next();
			}
		}

		@Override
		public String toString() {
			return "SendNOP.Callback [sessionId=" + sessionId + ", " + (txStatus != null ? "txStatus=" + txStatus + ", " : "")
					+ (txStatusReport != null ? "txStatusReport=" + Arrays.toString(txStatusReport) : "") + "]";
		}
	}
	
	
}

package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.protocol.Buffer;

/**
 * 4.3.13 Set Default Command
 * 
 * This command is used to set the Z-Wave API Module to its default state. 
 * It means that the Z-Wave API Module will leave its current network and erase all information related to its current Z-Wave network (topology, network keys, HomeID, etc.).
 */
@CommandFrame(
		value = SetDefault.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_CALLBACK,
		requestClass = SetDefault.Request.class,
		callbackClass = SetDefault.Callback.class
	)
public class SetDefault {

	public final static int COMMAND_ID = 0x42;
	
	public static class Request extends SerializableCommand {
	
		private final int sessionId;
		
		public Request(int sessionId) {
			super(COMMAND_ID);
			this.sessionId = sessionId;
		}
		
		public Request(Buffer buffer) {
			super(COMMAND_ID);
			this.sessionId = buffer.next();
		}
		
		@Override
		public Buffer serialize() {
			return Buffer.of(sessionId);
		}

		@Override
		public String toString() {
			return "SetDefault.Request [sessionId=" + sessionId + "]";
		}
	}
	
	
	public static class Callback extends DeserializableCommand {
		
		private final int sessionId;
		
		/**
		 * @return the sessionId
		 */
		public int getSessionId() {
			return sessionId;
		}


		public Callback(Buffer buffer) {
			super(COMMAND_ID);
			this.sessionId = buffer.next();
		}


		@Override
		public String toString() {
			return "SetDefault.Callback [sessionId=" + sessionId + "]";
		}
	}
}

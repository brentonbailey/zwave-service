package com.bbailey.smarthome.zwave.api;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.LibraryType;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.api.common.Version;
import com.bbailey.smarthome.zwave.protocol.Buffer;

@CommandFrame(
		value = GetLibraryVersion.COMMAND_ID, 
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE, 
		requestClass = GetLibraryVersion.Request.class, 
		responseClass = GetLibraryVersion.Response.class
	)
public class GetLibraryVersion {
	
	public final static int COMMAND_ID = 0x15;
	
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
		
		private final Version libraryVersion;
		private final LibraryType libraryType;
		
		
		/**
		 * @return the libraryVersion
		 */
		public Version getLibraryVersion() {
			return libraryVersion;
		}
	
		/**
		 * @return the libraryType
		 */
		public LibraryType getLibraryType() {
			return libraryType;
		}
	
	
	
	
		public Response(Buffer buffer) {
			super(COMMAND_ID);
			
			byte[] b = new byte[12];
			for (int i = 0 ; i < 12 ; i++) {
				b[i] = (byte)buffer.next();
			}
			
			this.libraryVersion = new Version(new String(b));
			libraryType = LibraryType.fromProtocol(buffer.next());
		}

		@Override
		public String toString() {
			return "GetLibraryVersion.Response [" + (libraryVersion != null ? "libraryVersion=" + libraryVersion + ", " : "")
					+ (libraryType != null ? "libraryType=" + libraryType : "") + "]";
		}
	}
}

package com.bbailey.smarthome.zwave.api;

import java.util.HashSet;
import java.util.Set;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.api.common.SerializableCommand;
import com.bbailey.smarthome.zwave.api.common.Version;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * 4.3.5 Get Capabilities Command
 * This command is used to request the API capabilities of a Z-Wave Module.
 */
@CommandFrame(
		value = GetCapabilities.COMMAND_ID,
		flow = CommandFlow.ACK_FRAME_WITH_RESPONSE,
		requestClass = GetCapabilities.Request.class,
		responseClass = GetCapabilities.Response.class
	)
public class GetCapabilities {
	
	public final static int COMMAND_ID = 0x07;
	
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
		
	
		private final Version apiVersion;
		private final int manufacturerId;
		private final int productType;
		private final int productId;
		private final Set<Integer> supportedCommands = new HashSet<>();
		
		
		/**
		 * @return the apiVersion
		 */
		public Version getApiVersion() {
			return apiVersion;
		}
	
	
		/**
		 * @return the manufacturerId
		 */
		public int getManufacturerId() {
			return manufacturerId;
		}
	
	
		/**
		 * @return the productType
		 */
		public int getProductType() {
			return productType;
		}
	
	
		/**
		 * @return the productId
		 */
		public int getProductId() {
			return productId;
		}
	
	
		/**
		 * @return the supportedCommands
		 */
		public Set<Integer> getSupportedCommands() {
			return supportedCommands;
		}
	
	
		public Response(Buffer buffer) {
			
			super(COMMAND_ID);
			
			apiVersion = new Version(buffer.next(), buffer.next());
			manufacturerId = BitUtils.convertToInt16(buffer.next(), buffer.next());
			productType = BitUtils.convertToInt16(buffer.next(), buffer.next());
			productId = BitUtils.convertToInt16(buffer.next(), buffer.next());
			
			
			int baseCommand = 1;
			while (buffer.hasNext()) {
				
				int mask = buffer.next();
				for (int b = 0 ; b < 8 ; b++) {
					if ((mask & (0x01 << b)) == 1) {
						supportedCommands.add(baseCommand + b);
					}
				}
				
				baseCommand += 8;
			}
		}


		@Override
		public String toString() {
			return "GetCapabilities.Response [" + (apiVersion != null ? "apiVersion=" + apiVersion + ", " : "") + "manufacturerId="
					+ manufacturerId + ", productType=" + productType + ", productId=" + productId + ", "
					+ (supportedCommands != null ? "supportedCommands=" + supportedCommands : "") + "]";
		}
		
		
	}

}

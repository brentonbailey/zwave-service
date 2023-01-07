package com.bbailey.smarthome.zwave.protocol;

import com.bbailey.smarthome.zwave.api.common.SerializableCommand;

public class DataFrame extends ZwaveFrame {

	public enum CommandType {
		/**
		 * The host application MUST use this for unsolicited new commands.
		 * Z-Wave API callbacks MUST also use the REQUEST type
		 */
		REQUEST(0x00),
		/**
		 * The type MUST be used by Z-Wave modules to issue responses to REQUEST frames
		 */
		RESPONSE(0x01);
		
		final int protocolValue;
		
		public int getProtocolValue() {
			return protocolValue;
		}
		
		CommandType(int protocolValue) {
			this.protocolValue = protocolValue;
		}
	}
	
	
	// The number of bytes in the remainder of the frame - 1 byte
	private int length;
	
	// The type of command being sent - 1 byte
	private CommandType type;
	
	// Advertises the command id to allow the reader to parse the payload correctly - 1 byte
	private int commandId;
	
	// The  payload of the API command - N bytes (based on the commandId being parsed)
	private Buffer commandPayload;
	
	// The message checksum - 1 byte
	// Checksum includes the length, type, commandId and commandPayload fields.
	private int checksum;

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the type
	 */
	public CommandType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(CommandType type) {
		this.type = type;
	}

	/**
	 * @return the commandId
	 */
	public int getCommandId() {
		return commandId;
	}

	/**
	 * @param commandId the commandId to set
	 */
	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	/**
	 * @return the commandPayload
	 */
	public Buffer getCommandPayload() {
		return commandPayload;
	}

	/**
	 * @param commandPayload the commandPayload to set
	 */
	public void setCommandPayload(Buffer commandPayload) {
		this.commandPayload = commandPayload;
	}

	/**
	 * @return the checksum
	 */
	public int getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}
	
	public DataFrame() {
		super(FrameType.SOF);
	}
	

	public DataFrame(SerializableCommand command) {
		this();
		this.type = CommandType.REQUEST;
		this.commandId = command.getCommandId();
		this.commandPayload = command.serialize();
		this.length = commandPayload.length() + 3;
	}
	
	
	public DataFrame(int length, byte[] buffer) {
		super(FrameType.SOF);
		this.length = length;
		
		int idx = 0;
		this.type = buffer[idx++] == CommandType.REQUEST.getProtocolValue() ? CommandType.REQUEST : CommandType.RESPONSE;
		this.commandId = buffer[idx++];
		
		this.commandPayload = new Buffer(length - 3);
		for (int i = 0 ; i < this.commandPayload.length() ; i++) {
			commandPayload.set(i, buffer[idx++]);
		}
		
		this.checksum = buffer[idx++];
	}
	
	@Override
	public byte[] serialize() {
		
		byte[] packet = new byte[length + 2];
		
		packet[0] = (byte)(getFrameType().getProtocolValue() & 0xFF);
		packet[1] = (byte)(length & 0xFF);
		packet[2] = (byte)(type.getProtocolValue() & 0xFF);
		packet[3] = (byte)(commandId & 0xFF);
		for (int i = 0 ; i < commandPayload.length() ; i++) {
			packet[4 + i] = (byte)commandPayload.get(i);
		}
		
		int checksum = calculateChecksum();
		packet[packet.length - 1] = (byte)(checksum & 0xFF);
		
		return packet;
	}
	
	
	public int calculateChecksum() {
		
		byte checksum = (byte)0xFF;
		checksum = (byte)(checksum ^ (byte)getLength());
		checksum = (byte)(checksum ^ (byte)getType().getProtocolValue());
		checksum = (byte)(checksum ^ (byte)getCommandId());
		for (int i = 0 ; i < commandPayload.length() ; i++) {
			checksum = (byte)(checksum ^ (byte)commandPayload.get(i));
		}
		
		return checksum;
	}
}

package com.bbailey.smarthome.zwave.protocol;

public class AcknowledgeFrame extends ZwaveFrame {

	public AcknowledgeFrame() {
		super(FrameType.ACK);
	}
	
	@Override
	public byte[] serialize() {
		byte b = (byte)(getFrameType().getProtocolValue() & 0xFF);
		return new byte[] { b };
	}
}

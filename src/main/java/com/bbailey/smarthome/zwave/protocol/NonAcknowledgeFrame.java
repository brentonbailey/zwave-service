package com.bbailey.smarthome.zwave.protocol;

public class NonAcknowledgeFrame extends ZwaveFrame {

	public NonAcknowledgeFrame() {
		super(FrameType.NAK);
	}
	
	@Override
	public byte[] serialize() {
		byte b = (byte)(getFrameType().getProtocolValue() & 0xFF);
		return new byte[] { b };
	}
}

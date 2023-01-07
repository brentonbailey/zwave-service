package com.bbailey.smarthome.zwave.protocol;

public class CancelFrame extends ZwaveFrame {

	public CancelFrame() {
		super(FrameType.CAN);
	}
	
	@Override
	public byte[] serialize() {
		byte b = (byte)(getFrameType().getProtocolValue() & 0xFF);
		return new byte[] { b };
	}

}

package com.bbailey.smarthome.zwave.protocol;

import java.util.Optional;

/**
 * Representation of a Z0Wave protocol frame. Specification in
 * Z-Wave Host API Specification.pdf
 */
public abstract class ZwaveFrame {

	public enum FrameType {
		/**
		 * Data Frames MUST start with SOF
		 */
		SOF(0x01),
		/**
		 * ACK frames are used to indicate the successful reception of a Data Frame
		 */
		ACK(0x06),
		/**
		 * NAK frames are used to indicate an error in the reception of a Data Frame
		 */
		NAK(0x15),
		/**
		 * A CAN frame is used to indicate the detection of a collision during Data Frame transmissions
		 */
		CAN(0x18);
		
		final int protocolValue;
		
		public int getProtocolValue() {
			return protocolValue;
		}
		
		FrameType(int protocolValue) {
			this.protocolValue = protocolValue;
		}
		
		public static Optional<FrameType> fromProtocolValue(int value) {
			for (FrameType frameType : FrameType.values()) {
				if (frameType.getProtocolValue() == value) {
					return Optional.of(frameType);
				}
			}
			
			return Optional.empty();
		}
	}
	
	
	
	// The type of frame - 1 byte
	private final FrameType frameType;
	
	
	/**
	 * @return the frameType
	 */
	public FrameType getFrameType() {
		return frameType;
	}



	public ZwaveFrame(FrameType frameType) {
		this.frameType = frameType;
	}
	
	
	public abstract byte[] serialize();
	
	public static ZwaveFrame of(FrameType frameType) {
		switch (frameType) {
		case ACK:
			return new AcknowledgeFrame();
		case CAN:
			return new CancelFrame();
		case NAK:
			return new NonAcknowledgeFrame();
		case SOF:
			return new DataFrame();
		default:
			throw new IllegalArgumentException("Unknown FrameType: " + frameType);
		}
	}
}

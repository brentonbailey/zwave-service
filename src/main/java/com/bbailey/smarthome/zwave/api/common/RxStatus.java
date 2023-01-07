package com.bbailey.smarthome.zwave.api.common;

import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * The Rx Status field is used to indicate how a Z-Wave frame was received.
 */
public enum RxStatus {
	/**
	 * This bit indicates if the Z-Wave frame has been received with low output power.
	 * • The value 0 MUST indicate that the frame was received with normal output power.
	 * • The value 1 MUST indicate that the frame was received with low output power.
	 */
	LOW_POWER(BitUtils.BIT_1),
	/**
	 * This bit indicates if the Z-Wave frame has been received using broadcast addressing.
	 * • The value 0 MUST indicate that the frame was received using multicast or singlecast addressing.
	 * • The value 1 MUST indicate that the frame was received using broadcast addressing.
	 */
	BROADCAST_ADDRESSING(BitUtils.BIT_3);
	
	private final int bitMask;
	
	private RxStatus(int bitMask) {
		this.bitMask = bitMask;
	}
	
	public boolean matches(int value) {
		return (value & bitMask) > 0;
	}
}

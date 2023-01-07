package com.bbailey.smarthome.zwave.api.common;

public enum CommandFlow {
	/**
	 * There may be data frames that will not be acknowledged by the destination.
	 * This can happen when the z-wave command instructs the z-wave module to go offline
	 */
	UNACK_FRAME(false, false),
	/**
	 * Frames that do not trigger any further communication to the z-wave module apart from an ACK Frame
	 */
	ACK_FRAME(false, false),
	/**
	 * Frames with a response are acknowledged frames that will trigger an immediate
	 * response from the Z-Wave module.
	 */
	ACK_FRAME_WITH_RESPONSE(true, false),
	/**
	 * Acknowledged frames that will trigger a callback after an operation
	 * has been performed by the Z-Wave module.
	 */
	ACK_FRAME_WITH_CALLBACK(false, true),
	/**
	 * Acknowledged frames that will trigger both an immediate response and a callback after
	 * the module has performed an action
	 */
	ACK_FRAME_WITH_RESPONSE_AND_CALLBACK(true, true),
	/**
	 * The z-wave module initiated the request off it's own back
	 */
	UNSOLICITED(false, false);
	
	private final boolean hasResponse;
	private final boolean hasCallback;
	
	
	
	/**
	 * @return the hasResponse
	 */
	public boolean hasResponse() {
		return hasResponse;
	}



	/**
	 * @return the hasCallback
	 */
	public boolean hasCallback() {
		return hasCallback;
	}



	private CommandFlow(boolean hasResponse, boolean hasCallback) {
		this.hasResponse = hasResponse;
		this.hasCallback = hasCallback;
	}
	
	
}

package com.bbailey.smarthome.zwave.api.common;

public enum TxStatus {
	/**
	 * Transmission completed and successful This value is used to indicate that the transmission was successful, 
	 * and acknowledged if an acknowledged transmission was requested.
	 */
	TRANSMIT_COMPLETE_OK(0x00),
	/**
	 * Transmission completed but no Acknowledgment This value is used to indicate that the transmission was completed,
	 * but no Acknowledgment has been received from the destination.
	 */
	TRANSMIT_COMPLETE_NO_ACK(0x01),
	/**
	 * Transmission failed. This value is used to indicate that the transmission couldnot be done.
	 */
	TRANSMIT_COMPLETE_FAIL(0x02),
	/**
	 * Transmission failed due to routing being busy. This value is used to indicate that
	 * the transmission could not be done due to routing being locked/busy.
	 */
	TRANSMIT_ROUTING_NOT_IDLE(0x03),
	/**
	 * Transmission failed due to routing resolution. This value is used to indicate that the 
	 * transmission could not be done due to missing route or failed route resolution.
	 */
	TRANSMIT_COMPLETE_NOROUTE(0x04),
	/**
	 * Transmission completed and successful, including S2 resynchronization backoff 
	 * This value is used to indicate that the transmission was successful, and acknowledged 
	 * and that the destination has successfully decrypted the message.
	 * This status MUST be used only if the Z-Wave module performed Security encryption.
	 */
	TRANSMIT_COMPLETE_VERIFIED(0x05),
	/**
	 * Catch all for when the z-wave module is not configured for a report
	 */
	TRANSMIT_NO_REPORT(0xFF);
	
	final int protocolValue;
	
	private TxStatus(int protocolValue) {
		this.protocolValue = protocolValue;
	}
	
	public static TxStatus fromProtocolValue(int protocolValue) {
		for (TxStatus status : TxStatus.values()) {
			if (status.protocolValue == protocolValue) {
				return status;
			}
		}
		
		return TxStatus.TRANSMIT_NO_REPORT;
	}
}

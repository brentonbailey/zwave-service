package com.bbailey.smarthome.zwave.api.common;

import java.util.Optional;

public class RssiMeasurement {

	private final boolean belowSensitivity;
	private final boolean receiverSaturated;
	private final boolean rssiNotAvailable;
	private final Integer rssiValue;
	
	
	/**
	 * The RSSI is below sensitivity and could not be measured.
	 * @return the belowSensitivity
	 */
	public boolean isBelowSensitivity() {
		return belowSensitivity;
	}
	
	
	/**
	 * The radio receiver is saturated and the RSSI could not be measured.
	 * @return the receiverSaturated
	 */
	public boolean isReceiverSaturated() {
		return receiverSaturated;
	}
	
	
	/**
	 * The RSSI is not available.
	 * @return the rssiNotAvailable
	 */
	public boolean isRssiNotAvailable() {
		return rssiNotAvailable;
	}
	
	
	/**
	 * The measured RSSI in dBm
	 * @return the rssiValue
	 */
	public Optional<Integer> getRssiValue() {
		return Optional.ofNullable(rssiValue);
	}
	
	
	public RssiMeasurement(int protocolValue) {
		
		this.belowSensitivity = protocolValue == 0x7D;
		this.receiverSaturated = protocolValue == 0x7E;
		this.rssiNotAvailable = protocolValue == 0x7F;
		
		if (protocolValue <= 0x7C) {
			this.rssiValue = protocolValue;
		} else if (protocolValue >= 0x80) {
			this.rssiValue = protocolValue - 0x100;
		} else {
			// there is no rssi reading
			this.rssiValue = null;
		}
	}


	@Override
	public String toString() {
		return "RssiMeasurement [belowSensitivity=" + belowSensitivity + ", receiverSaturated=" + receiverSaturated
				+ ", rssiNotAvailable=" + rssiNotAvailable + ", " + (rssiValue != null ? "rssiValue=" + rssiValue : "")
				+ "]";
	}
}

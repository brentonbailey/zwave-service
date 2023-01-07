package com.bbailey.smarthome.zwave.api.common;

import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * This field is used to indicate the Z-Wave RF Region, defining the number of channels and center 
 * frequency on which the Z-Wave API Module operates.
 */
public enum RfRegion {
	EU(0x01),
	US(0x01),
	ANZ(0x02),
	HK(0x03),
	IN(0x04),
	IL(0x05),
	RU(0x06),
	CN(0x07),
	US_LONG_RANGE(0x09),
	JP(0x20),
	KR(0x21),
	/**
	 * Undefined/unknown region. This value can be used if there was an error retrieving the configured region.
	 */
	UNKNOWN(0xFE);
	
	private final int protocolValue;
	
	private RfRegion(int protocolValue) {
		this.protocolValue = protocolValue;
	}
	
	public static RfRegion fromProtocolValue(int protocolValue) {
		
		if (protocolValue == 0xFF) {
			// This inidcates the default region which is EU
			return RfRegion.EU;
		}
		
		for (RfRegion region : RfRegion.values()) {
			if (region.protocolValue == protocolValue) {
				return region;
			}
		}
		
		throw new IllegalArgumentException("Unsupported RF Region: " + BitUtils.toHex(protocolValue));
	}
}

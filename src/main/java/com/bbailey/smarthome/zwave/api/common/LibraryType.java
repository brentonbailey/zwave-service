package com.bbailey.smarthome.zwave.api.common;

public enum LibraryType {
	STATIC_CONTROLLER(0x01),
	PORTABLE_CONTROLLER(0x02),
	ENHANCED_END_NODE(0x03),
	END_NODE(0x04),
	INSTALLER(0x05),
	ROUTING_END_NODE(0x06),
	BRIDGE_CONTROLLER(0x07),
	UNKNOWN(-1);
	
	final int protocolValue;
	
	LibraryType(int protocolValue) {
		this.protocolValue = protocolValue;
	}
	
	public static LibraryType fromProtocol(int value) {
		for (LibraryType libraryType : LibraryType.values()) {
			if (libraryType.protocolValue == (value & 0xFF)) {
				return libraryType;
			}
		}
		
		return LibraryType.UNKNOWN;
	}
}
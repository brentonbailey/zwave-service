package com.bbailey.smarthome.zwave.device;

/**
 * 4 BASIC DEVICE CLASSES
 * 
 * Z-Wave Device Class Specification
 */
public enum BasicDeviceType {
	
	/**
	 * Node is a portable controller.
	 */
    BASIC_TYPE_CONTROLLER(0x01),
    /**
     * Node is a static controller.
     */
    BASIC_TYPE_STATIC_CONTROLLER(0x02),
    /**
     * End node
     */
    BASIC_TYPE_SLAVE(0x03),
    /**
     * End node with routing capabilities
     */
    BASIC_TYPE_ROUTING_SLAVE(0x04),
    /**
     * Reserved values 0x05..0xFF must not be used
     */
	BASIC_TYPE_UNKNOWN(Integer.MAX_VALUE);
    
	
	final int protocolValue;
	
	private BasicDeviceType(int protocolValue) {
		this.protocolValue = protocolValue;
	}
	
	public static BasicDeviceType fromProtocolValue(int protcolValue) {
		
		for (BasicDeviceType basicDeviceType : BasicDeviceType.values()) {
			if (basicDeviceType.protocolValue == protcolValue) {
				return basicDeviceType;
			}
		}
		
		return BasicDeviceType.BASIC_TYPE_UNKNOWN;
	}
}

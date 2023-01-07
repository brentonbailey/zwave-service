package com.bbailey.smarthome.zwave.api.common;

/**
 * This field is used to advertise the routed packet data rate that shall be used through the return route
 */
public enum RouteSpeed {
	/**
	 * This flag indicates that the priority route MUST use a data rate of 9.6 kbits/seconds.
	 */
	ROUTE_SPEED_9600(0x01),
	/**
	 * This flag indicates that the priority route MUST use a data rate of 40 kbits/seconds.
	 */
	ROUTE_SPEED_40K(0x02),
	/**
	 * This flag indicates that the priority route MUST use a data rate of 100 kbits/seconds.
	 */
	ROUTE_SPEED_100K(0x03),
	ROUTE_SPEED_IGNORE(-1);
	
	private final int protocolValue;
	
	private RouteSpeed(int protocolValue) {
		this.protocolValue = protocolValue;
	}
	
	public static RouteSpeed fromProtocolValue(int protocolValue) {
		for (RouteSpeed speed : RouteSpeed.values()) {
			if (speed.protocolValue == protocolValue) {
				return speed;
			}
		}
		
		// All other values should be ignored
		return RouteSpeed.ROUTE_SPEED_IGNORE;
	}
}

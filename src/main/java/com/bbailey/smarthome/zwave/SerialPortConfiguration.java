package com.bbailey.smarthome.zwave;

import javax.validation.constraints.NotNull;

public class SerialPortConfiguration {

	@NotNull
	private String portName = "ttyAMA0";

	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	
}

package com.bbailey.smarthome.zwave.api.common;

public abstract class Command {

	private final int commandId;
	
	
	/**
	 * @return the commandId
	 */
	public int getCommandId() {
		return commandId;
	}
	

	protected Command(int commandId) {
		this.commandId = commandId;
	}
	
	
	
}

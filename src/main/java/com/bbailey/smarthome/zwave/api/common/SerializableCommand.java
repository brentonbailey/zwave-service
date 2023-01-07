package com.bbailey.smarthome.zwave.api.common;

import com.bbailey.smarthome.zwave.protocol.Buffer;

public abstract class SerializableCommand extends Command {

	public SerializableCommand(int commandId) {
		super(commandId);
	}
	
	public abstract Buffer serialize();
}

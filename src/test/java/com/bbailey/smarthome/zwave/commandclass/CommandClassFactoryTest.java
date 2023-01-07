package com.bbailey.smarthome.zwave.commandclass;

import org.junit.jupiter.api.Test;

public class CommandClassFactoryTest {

	@Test
	public void test() {
		
		CommandClass commandClass = CommandClassFactory.create(null, CommandClasses.COMMAND_CLASS_WAKE_UP, 1);
		commandClass = commandClass;
	}
}

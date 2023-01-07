package com.bbailey.smarthome.zwave.api.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandFrame {

	/**
	 * The numeric value of the class
	 * @return The numeric value of the class
	 */
	int value();
	
	/**
	 * The class that represents requests of this command class
	 * @return The request class
	 */
	Class<? extends Command> requestClass();
	
	/**
	 * The class that represents responses of this command class
	 * @return The response class
	 */
	Class<? extends DeserializableCommand> responseClass() default NoResponse.class;
	
	/**
	 * The class that represents callbacks of this command class
	 * @return The callback class
	 */
	Class<? extends DeserializableCommand> callbackClass() default NoResponse.class;
	
	/**
	 * The expected flow of the command
	 * @return The flow we expect
	 */
	CommandFlow flow() default CommandFlow.ACK_FRAME;
	
	
}

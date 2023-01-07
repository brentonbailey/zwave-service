package com.bbailey.smarthome.zwave.commandclass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandClassMeta {

	int id();
	
	String name();
	
	String desc() default "";
}

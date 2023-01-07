package com.bbailey.smarthome.zwave.commandclass;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;

@CommandClassMeta(id = BasicCommandClass.COMMAND_ID, name = "COMMAND_CLASS_BASIC", desc = "Command Class Basic")
public class BasicCommandClass extends CommandClass {

	private final static Logger LOGGER = LoggerFactory.getLogger(BasicCommandClass.class);
	
	public final static int COMMAND_ID = 0x20;
	
	private final static int BASIC_SET = 0x01;
	private final static int BASIC_GET = 0x02;
	private final static int BASIC_REPORT = 0x03;
	
	// Version 1
	private Integer value;
	
	// Version 2
	private Integer targetValue;
	private Integer duration;
	
	
	
	/**
	 * @return the value
	 */
	protected Integer getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	protected void setValue(Integer value) {
		this.value = value;
	}

	/**
	 * @return the targetValue
	 */
	protected Integer getTargetValue() {
		return targetValue;
	}

	/**
	 * @param targetValue the targetValue to set
	 */
	protected void setTargetValue(Integer targetValue) {
		this.targetValue = targetValue;
	}

	/**
	 * @return the duration
	 */
	protected Integer getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	protected void setDuration(Integer duration) {
		this.duration = duration;
	}

	public BasicCommandClass(int version, ZwaveNode node) {
		super(COMMAND_ID, version, node);
	}
	
	public void sendGet() {
		send(Buffer.of(COMMAND_ID, BASIC_GET));
	}
	
	public void sendSet(int value) {
		send(Buffer.of(COMMAND_ID, BASIC_SET, value));
	}
	
	@ReportHandler(id = BASIC_REPORT)
	public void handlReport(Buffer buffer) {
		setValue(buffer.next());
		if (buffer.hasNext()) {
			// Version 2 fields
			setTargetValue(buffer.next());
			setDuration(buffer.next());
		}
		
		LOGGER.info("BASIC: value = {}, target = {}, duration = {}", value, targetValue, duration);
	}
	
}

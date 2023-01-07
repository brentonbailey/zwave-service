package com.bbailey.smarthome.zwave.commandclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;

@CommandClassMeta(id = BatteryCommandClass.COMMAND_ID, name = "COMMAND_CLASS_BATTERY", desc = "Command Class Battery")
public class BatteryCommandClass extends CommandClass {

	private final static Logger LOGGER = LoggerFactory.getLogger(BatteryCommandClass.class);
	
	public final static int COMMAND_ID = 0x80;
	
	private final static int BATTERY_GET = 0x02;
	private final static int BATTERY_REPORT = 0x03;
	
	private Integer value;
	
	
	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}
	
	public boolean isLowBattery() {
		if (value == null) {
			return false;
		}
		
		return value == 0xFF;
	}

	/**
	 * @param value the value to set
	 */
	private void setValue(Integer value) {
		this.value = value;
	}


	public BatteryCommandClass(int version, ZwaveNode node) {
		super(COMMAND_ID, version, node);
	}
	
	
	public void sendGetBattery() {
		send(Buffer.of(COMMAND_ID, BATTERY_GET));
	}
	
	@ReportHandler(id = BATTERY_REPORT)
	protected void handleReport(Buffer buffer) {
		setValue(buffer.next());
		LOGGER.info("Battery value {}", value);
	}
}

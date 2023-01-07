package com.bbailey.smarthome.zwave.commandclass;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

@CommandClassMeta(id = WakeupCommandClass.COMMAND_ID, name = "COMMAND_CLASS_WAKEUP", desc = "Command Class Wake Up")
public class WakeupCommandClass extends CommandClass {

	private static final Logger LOGGER = LoggerFactory.getLogger(WakeupCommandClass.class);
	
	public static final int COMMAND_ID = 0x84;
	
	// Version 1
	private static final int WAKE_UP_INTERVAL_SET = 0x04;
	private static final int WAKE_UP_INTERVAL_GET = 0x05;
	private static final int WAKE_UP_INTERVAL_REPORT = 0x06;
	private static final int WAKE_UP_NOTIFICATION = 0x07;
	private static final int WAKE_UP_NO_MORE_INFORMATION = 0x08;
	
	// Version 2
	private static final int WAKE_UP_INTERVAL_CAPABILITIES_GET = 0x09;
	private static final int WAKE_UP_INTERVAL_CAPABILTIES_REPORT = 0x0A;
	
	// Version 1 field
	private Duration interval;
	
	// Version 2 field
	private Duration minimumInterval;	
	private Duration maximumInterval;
	private Duration defaultInterval;
	private Integer step;
	
	
	
	/**
	 * @return the interval
	 */
	public Duration getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	private void setInterval(Duration interval) {
		this.interval = interval;
	}

	/**
	 * @return the minimumInterval
	 */
	public Duration getMinimumInterval() {
		return minimumInterval;
	}

	/**
	 * @param minimumInterval the minimumInterval to set
	 */
	private void setMinimumInterval(Duration minimumInterval) {
		this.minimumInterval = minimumInterval;
	}

	/**
	 * @return the maximumInterval
	 */
	public Duration getMaximumInterval() {
		return maximumInterval;
	}

	/**
	 * @param maximumInterval the maximumInterval to set
	 */
	private void setMaximumInterval(Duration maximumInterval) {
		this.maximumInterval = maximumInterval;
	}

	/**
	 * @return the defaultInterval
	 */
	public Duration getDefaultInterval() {
		return defaultInterval;
	}

	/**
	 * @param defaultInterval the defaultInterval to set
	 */
	private void setDefaultInterval(Duration defaultInterval) {
		this.defaultInterval = defaultInterval;
	}

	/**
	 * @return the step
	 */
	public Integer getStep() {
		return step;
	}

	/**
	 * @param step the step to set
	 */
	private void setStep(Integer step) {
		this.step = step;
	}

	public WakeupCommandClass(int version, ZwaveNode node) {
		super(COMMAND_ID, version, node);
	}
	
	public void sendGetInterval() {
		send(Buffer.of(COMMAND_ID, WAKE_UP_INTERVAL_GET));
	}
	
	public void sendGetIntervalCapabilities() {
		if (getSupportedVersion() < 2) {
			throw new IllegalStateException("Command " + WAKE_UP_INTERVAL_CAPABILITIES_GET + " not supported for version " + getSupportedVersion());
		}
		send(Buffer.of(COMMAND_ID, WAKE_UP_INTERVAL_CAPABILITIES_GET));
	}
	
	@ReportHandler(id = WAKE_UP_INTERVAL_REPORT)
	protected void handleIntervalReport(Buffer buffer) {
		setInterval(Duration.ofSeconds(buffer.nextInt24()));
		LOGGER.info("Interval: {}", interval);
	}
	
	
	@ReportHandler(id = WAKE_UP_INTERVAL_CAPABILTIES_REPORT)
	protected void handleIntervalCapabilitiesReport(Buffer buffer) {
		setMinimumInterval(Duration.ofSeconds(buffer.nextInt24()));
		setMaximumInterval(Duration.ofSeconds(buffer.nextInt24()));
		setDefaultInterval(Duration.ofSeconds(buffer.nextInt24()));
		setStep(buffer.nextInt24());
		LOGGER.info("Min Interval: {}, Max Interval {}, Default Interval {}, step {}", minimumInterval, maximumInterval, defaultInterval, step);
	}
	
	
	@ReportHandler(id = WAKE_UP_NOTIFICATION)
	protected void handleNotification(Buffer buffer) {
		LOGGER.info("Node AWAKE");
	}
	
	public void sendSetInterval(int seconds) {
		
		Buffer buffer = Buffer.of(
				COMMAND_ID,
				WAKE_UP_INTERVAL_SET,
				BitUtils.byte2(seconds),
				BitUtils.byte1(seconds),
				BitUtils.byte0(seconds),
				getNode().getNodeId()
			);
		
		send(buffer);
	}
	
	
	public void sendWakeupNoMoreInformation() {
		send(Buffer.of(COMMAND_ID, WAKE_UP_NO_MORE_INFORMATION));
	}
}

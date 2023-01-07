package com.bbailey.smarthome.zwave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.annotation.Validated;

import com.bbailey.smarthome.zwave.device.ZwaveController;
import com.fazecast.jSerialComm.SerialPort;

@Configuration
public class ZwaveConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZwaveConfig.class);
	
	
	@Bean
	public SerialPort serialPort(SerialPortConfiguration config) {
		
		SerialPort serialPort = SerialPort.getCommPort(config.getPortName());
		if (serialPort == null) {
			LOGGER.error("Serial Port {} not found", config.getPortName());
			throw new IllegalArgumentException("Cannot not find serial port " + config.getPortName());
		}
		
		boolean ok = serialPort.setComPortParameters(115200, 8, 1, 0);
		if (!ok) {
			LOGGER.error("Serial port baudrate configuration not valid");
		}
		boolean configurationOK = serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 15000, 5000);
		if (!configurationOK) {
			LOGGER.error("Serial port timeout configuration is not valid");
		}
		
		LOGGER.info("Serial port configured");
		return serialPort;
	}
	
	
	@Bean
	@Order(value = Ordered.HIGHEST_PRECEDENCE)
	public ZwaveAdapter zwaveAdapter(SerialPort serialPort) {
		return new ZwaveAdapter(serialPort);
	}
	
	
	@Bean
	@Validated
	@ConfigurationProperties(prefix = "zwave.serial")
	public SerialPortConfiguration serialConfiguration() {
		return new SerialPortConfiguration();
	}
	
	
	@Bean
	public ZwaveController zwaveController(ZwaveAdapter adapter) {
		return new ZwaveController(adapter);
	}
}

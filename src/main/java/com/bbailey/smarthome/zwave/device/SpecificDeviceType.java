package com.bbailey.smarthome.zwave.device;

public enum SpecificDeviceType {

	SPECIFIC_TYPE_NOT_USED(GenericDeviceType.GENERIC_TYPE_NOT_USED, 0x00),
	SPECIFIC_TYPE_DOORBELL(GenericDeviceType.GENERIC_TYPE_AV_CONTROL_POINT, 0x12),
	SPECIFIC_TYPE_SATELLITE_RECEIVER(GenericDeviceType.GENERIC_TYPE_AV_CONTROL_POINT, 0x04),
	SPECIFIC_TYPE_SATELLITE_RECEIVER_V2(GenericDeviceType.GENERIC_TYPE_AV_CONTROL_POINT, 0x11),
	SPECIFIC_TYPE_SOUND_SWITCH(GenericDeviceType.GENERIC_TYPE_AV_CONTROL_POINT, 0x01),
	
	SPECIFIC_TYPE_SIMPLE_DISPLAY(GenericDeviceType.GENERIC_TYPE_DISPLAY, 0x01),
	
	SPECIFIC_TYPE_DOOR_LOCK(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x01),
	SPECIFIC_TYPE_ADVANCED_DOOR_LOCK(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x02),
	SPECIFIC_TYPE_SECURE_KEYPAD_DOOR_LOCK(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x03),
	SPECIFIC_TYPE_SECURE_KEYPAD_DOOR_LOCK_DEADBOLT(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x04),
	SPECIFIC_TYPE_SECURE_DOOR(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x05),
	SPECIFIC_TYPE_SECURE_GATE(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x06),
	SPECIFIC_TYPE_SECURE_BARRIER_ADDON(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x07),
	SPECIFIC_TYPE_SECURE_BARRIER_OPEN_ONLY(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x08),
	SPECIFIC_TYPE_SECURE_BARRIER_CLOSE_ONLY(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x09),
	SPECIFIC_TYPE_SECURE_LOCKBOX(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x0A),
	SPECIFIC_TYPE_SECURE_KEYPAD(GenericDeviceType.GENERIC_TYPE_ENTRY_CONTROL, 0x0B),
	
	SPECIFIC_TYPE_PORTABLE_REMOTE_CONTROLLER(GenericDeviceType.GENERIC_TYPE_GENERIC_CONTROLLER, 0x01),
	SPECIFIC_TYPE_PORTABLE_SCENE_CONTROLLER(GenericDeviceType.GENERIC_TYPE_GENERIC_CONTROLLER, 0x02),
	SPECIFIC_TYPE_PORTABLE_INSTALLER_TOOL(GenericDeviceType.GENERIC_TYPE_GENERIC_CONTROLLER, 0x03),
	SPECIFIC_TYPE_REMOTE_CONTROL_AV(GenericDeviceType.GENERIC_TYPE_GENERIC_CONTROLLER, 0x04),
	SPECIFIC_TYPE_REMOTE_CONTROL_SIMPLE(GenericDeviceType.GENERIC_TYPE_GENERIC_CONTROLLER, 0x06),
	
	SPECIFIC_TYPE_SIMPLE_METER(GenericDeviceType.GENERIC_TYPE_METER, 0x01),
	SPECIFIC_TYPE_ADV_ENERGY_CONTROL(GenericDeviceType.GENERIC_TYPE_METER, 0x02),
	SPECIFIC_TYPE_WHOLE_HOME_METER_SIMPLE(GenericDeviceType.GENERIC_TYPE_METER, 0x03),
	
	SPECIFIC_TYPE_REPEATER_SLAVE(GenericDeviceType.GENERIC_TYPE_REPEATER_SLAVE, 0x01),
	SPECIFIC_TYPE_VIRTUAL_NODE(GenericDeviceType.GENERIC_TYPE_REPEATER_SLAVE, 0x02),
	
	SPECIFIC_TYPE_ENERGY_PRODUCTION(GenericDeviceType.GENERIC_TYPE_SEMI_INTEROPERABLE, 0x01),
	
	SPECIFIC_TYPE_ADV_ZENSOR_NET_ALARM_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x05),
	SPECIFIC_TYPE_ADV_ZENSOR_NET_SMOKE_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x0A),
	SPECIFIC_TYPE_BASIC_ROUTING_ALARM_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x01),
	SPECIFIC_TYPE_BASIC_ROUTING_SMOKE_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x06),
	SPECIFIC_TYPE_BASIC_ZENSOR_NET_ALARM_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x03),
	SPECIFIC_TYPE_BASIC_ZENSOR_NET_SMOKE_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x08),
	SPECIFIC_TYPE_ROUTING_ALARM_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x02),
	SPECIFIC_TYPE_ROUTING_SMOKE_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x07),
	SPECIFIC_TYPE_ZENSOR_NET_ALARM_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x04),
	SPECIFIC_TYPE_ZENSOR_NET_SMOKE_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x09),
	SPECIFIC_TYPE_ALARM_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_ALARM, 0x0B),
	
	SPECIFIC_TYPE_ROUTING_SENSOR_BINARY(GenericDeviceType.GENERIC_TYPE_SENSOR_BINARY, 0x01),
	
	SPECIFIC_TYPE_ROUTING_SENSOR_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SENSOR_MULTILEVEL, 0x01),
	SPECIFIC_TYPE_CHIMNEY_FAN(GenericDeviceType.GENERIC_TYPE_SENSOR_MULTILEVEL, 0x02),
	
	SPECIFIC_TYPE_PC_CONTROLLER(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x01),
	SPECIFIC_TYPE_SCENE_CONTROLLER(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x02),
	SPECIFIC_TYPE_STATIC_INSTALLER_TOOL(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x03),
	SPECIFIC_TYPE_SET_TOP_BOX(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x04),
	SPECIFIC_TYPE_SUB_SYSTEM_CONTROLLER(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x05),
	SPECIFIC_TYPE_TV(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x06),
	SPECIFIC_TYPE_GATEWAY(GenericDeviceType.GENERIC_TYPE_STATIC_CONTROLLER, 0x07),
	
	SPECIFIC_TYPE_POWER_SWITCH_BINARY(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x01),
	SPECIFIC_TYPE_SCENE_SWITCH_BINARY(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x03),
	SPECIFIC_TYPE_POWER_STRIP(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x04),
	SPECIFIC_TYPE_SIREN(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x05),
	SPECIFIC_TYPE_VALVE_OPEN_CLOSE(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x06),
	SPECIFIC_TYPE_COLOR_TUNABLE_BINARY(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x02),
	SPECIFIC_TYPE_IRRIGATION_CONTROLLER(GenericDeviceType.GENERIC_TYPE_SWITCH_BINARY, 0x07),
	
	SPECIFIC_TYPE_CLASS_A_MOTOR_CONTROL(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x05),
	SPECIFIC_TYPE_CLASS_B_MOTOR_CONTROL(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x06),
	SPECIFIC_TYPE_CLASS_C_MOTOR_CONTROL(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x07),
	SPECIFIC_TYPE_MOTOR_MULTIPOSITION(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x03),
	SPECIFIC_TYPE_POWER_SWITCH_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x01),
	SPECIFIC_TYPE_SCENE_SWITCH_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x04),
	SPECIFIC_TYPE_FAN_SWITCH(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x08),
	SPECIFIC_TYPE_COLOR_TUNABLE_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SWITCH_MULTILEVEL, 0x02),
	
	SPECIFIC_TYPE_SWITCH_REMOTE_BINARY(GenericDeviceType.GENERIC_TYPE_SWITCH_REMOTE, 0x01),
	SPECIFIC_TYPE_SWITCH_REMOTE_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SWITCH_REMOTE, 0x02),
	SPECIFIC_TYPE_SWITCH_REMOTE_TOGGLE_BINARY(GenericDeviceType.GENERIC_TYPE_SWITCH_REMOTE, 0x03),
	SPECIFIC_TYPE_SWITCH_REMOTE_TOGGLE_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SWITCH_REMOTE, 0x04),
	
	SPECIFIC_TYPE_SWITCH_TOGGLE_BINARY(GenericDeviceType.GENERIC_TYPE_SWITCH_TOGGLE, 0x01),
	SPECIFIC_TYPE_SWITCH_TOGGLE_MULTILEVEL(GenericDeviceType.GENERIC_TYPE_SWITCH_TOGGLE, 0x02),
	
	SPECIFIC_TYPE_SETBACK_SCHEDULE_THERMOSTAT(GenericDeviceType.GENERIC_TYPE_THERMOSTAT, 0x03),
	SPECIFIC_TYPE_SETBACK_THERMOSTAT(GenericDeviceType.GENERIC_TYPE_THERMOSTAT, 0x05),
	SPECIFIC_TYPE_SETPOINT_THERMOSTAT(GenericDeviceType.GENERIC_TYPE_THERMOSTAT, 0x04),
	SPECIFIC_TYPE_THERMOSTAT_GENERAL(GenericDeviceType.GENERIC_TYPE_THERMOSTAT, 0x02),
	SPECIFIC_TYPE_THERMOSTAT_GENERAL_V2(GenericDeviceType.GENERIC_TYPE_THERMOSTAT, 0x06),
	SPECIFIC_TYPE_THERMOSTAT_HEATING(GenericDeviceType.GENERIC_TYPE_THERMOSTAT, 0x01),
	
	SPECIFIC_TYPE_RESIDENTIAL_HRV(GenericDeviceType.GENERIC_TYPE_VENTILATION, 0x01),
	
	SPECIFIC_TYPE_SIMPLE_WINDOW_COVERING(GenericDeviceType.GENERIC_TYPE_WINDOW_COVERING, 0x01),
	
	SPECIFIC_TYPE_ZIP_ADV_NODE(GenericDeviceType.GENERIC_TYPE_ZIP_NODE, 0x02),
	SPECIFIC_TYPE_ZIP_TUN_NODE(GenericDeviceType.GENERIC_TYPE_ZIP_NODE, 0x01),
	
	SPECIFIC_TYPE_BASIC_WALL_CONTROLLER(GenericDeviceType.GENERIC_TYPE_WALL_CONTROLLER, 0x01),
	
	SPECIFIC_TYPE_SECURE_EXTENDER(GenericDeviceType.GENERIC_TYPE_NETWORK_EXTENDER, 0x01),
	
	SPECIFIC_TYPE_GENERAL_APPLIANCE(GenericDeviceType.GENERIC_TYPE_APPLIANCE, 0x01),
	SPECIFIC_TYPE_KITCHEN_APPLIANCE(GenericDeviceType.GENERIC_TYPE_APPLIANCE, 0x02),
	SPECIFIC_TYPE_LAUNDRY_APPLIANCE(GenericDeviceType.GENERIC_TYPE_APPLIANCE, 0x03),
	
	SPECIFIC_TYPE_NOTIFICATION_SENSOR(GenericDeviceType.GENERIC_TYPE_SENSOR_NOTIFICATION, 0x01)
	;
	
	private final GenericDeviceType generic;
	private final int protocolVersion;
	
	private SpecificDeviceType(GenericDeviceType generic, int protocolVersion) {
		this.generic = generic;
		this.protocolVersion = protocolVersion;
	}
	
	public static SpecificDeviceType fromProtocolVersion(GenericDeviceType generic, int protocolVersion) {
		for (SpecificDeviceType specificDeviceType : SpecificDeviceType.values()) {
			if (specificDeviceType.generic == generic && specificDeviceType.protocolVersion == protocolVersion) {
				return specificDeviceType;
			}
		}
		
		return SPECIFIC_TYPE_NOT_USED;
	
	}
}

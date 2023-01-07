package com.bbailey.smarthome.zwave.api;

import java.util.ArrayList;
import java.util.List;

import com.bbailey.smarthome.zwave.api.common.CommandFlow;
import com.bbailey.smarthome.zwave.api.common.CommandFrame;
import com.bbailey.smarthome.zwave.api.common.DeserializableCommand;
import com.bbailey.smarthome.zwave.device.GenericDeviceType;
import com.bbailey.smarthome.zwave.device.SpecificDeviceType;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

@CommandFrame(
		value = ApiStarted.COMMAND_ID,
		flow = CommandFlow.UNSOLICITED,
		requestClass = ApiStarted.Request.class
	)
public class ApiStarted {

	public final static int COMMAND_ID = 0x0A;
	
	public enum WakeupReason {
		/**
		 * The Z-Wave API Module has been woken up by reset or external interrupt.
		 */
		RESET(0x00),
		/**
		 * The Z-Wave API Module has been woken up by a timer.
		 */
		WAKEUP_TIMER(0x01),
		/**
		 * The Z-Wave API Module has been woken up by a Wake Up Beam.
		 */
		WAKEUP_BEAM(0x02),
		/**
		 * The Z-Wave API Module has been woken up by a reset triggered by the watch-dog.
		 */
		WATCHDOG_RESET(0x03),
		/**
		 * The Z-Wave API Module has been woken up by an external interrupt.
		 */
		EXTERNAL_INTERRUPT(0x04),
		/**
		 * The Z-Wave API Module has been woken up by a powering up.
		 */
		POWER_UP(0x05),
		/**
		 * The Z-Wave API Module has been woken up by USB Suspend.
		 */
		USB_SUSPEND(0x06),
		/**
		 * The Z-Wave API Module has been woken up by a reset triggered by software.
		 */
		SOFTWARE_RESET(0x07),
		/**
		 * The Z-Wave API Module has been woken up by an emergency watchdog reset.
		 */
		EMERGENCY_WATCHDOG_RESET(0x08),
		/**
		 * The Z-Wave API Module has been woken up by a reset triggered by brownout circuit.
		 */
		BROWNOUT_CIRCUIT(0x09),
		/**
		 * The Z-Wave API Module has been woken up by an unknown reason.
		 */
		UNKNOWN(0xFF);
		
		private final int protocolValue;
		
		private WakeupReason(int protocolValue) {
			this.protocolValue = protocolValue;
		}
		
		public static WakeupReason fromProtocolValue(int protocolValue) {
			for (WakeupReason reason : WakeupReason.values()) {
				if (reason.protocolValue == protocolValue) {
					return reason;
				}
			}
			
			return WakeupReason.UNKNOWN;
		}
	}
	

	
	public static class Request extends DeserializableCommand {
		
		private final WakeupReason wakeupReason;
		private final boolean watchdogStarted;
		private final int deviceOptionMask;
		private final GenericDeviceType genericDeviceType;
		private final SpecificDeviceType specificDeviceType;
		private List<Integer> commandClasses;
		private final boolean supportsLongRange;
		
		/**
		 * @return the commandClasses
		 */
		public List<Integer> getCommandClasses() {
			return commandClasses;
		}

		/**
		 * @param commandClasses the commandClasses to set
		 */
		public void setCommandClasses(List<Integer> commandClasses) {
			this.commandClasses = commandClasses;
		}

		/**
		 * @return the wakeupReason
		 */
		public WakeupReason getWakeupReason() {
			return wakeupReason;
		}

		/**
		 * @return the watchdogStarted
		 */
		public boolean isWatchdogStarted() {
			return watchdogStarted;
		}

		/**
		 * @return the deviceOptionMask
		 */
		public int getDeviceOptionMask() {
			return deviceOptionMask;
		}

		/**
		 * @return the genericDeviceType
		 */
		public GenericDeviceType getGenericDeviceType() {
			return genericDeviceType;
		}

		/**
		 * @return the specificDeviceType
		 */
		public SpecificDeviceType getSpecificDeviceType() {
			return specificDeviceType;
		}

		/**
		 * @return the supportsLongRange
		 */
		public boolean isSupportsLongRange() {
			return supportsLongRange;
		}

		public Request(Buffer buffer) {
			super(COMMAND_ID);
			
			this.wakeupReason = WakeupReason.fromProtocolValue(buffer.next());
			this.watchdogStarted = buffer.next() == 0x01;
			this.deviceOptionMask = buffer.next();
			this.genericDeviceType = GenericDeviceType.fromProtocolVersion(buffer.next());
			this.specificDeviceType = SpecificDeviceType.fromProtocolVersion(genericDeviceType, buffer.next());
			
			int commandLength = buffer.next();
			this.commandClasses = new ArrayList<>(commandLength);
			for (int i = 0 ; i < commandLength ; i++) {
				this.commandClasses.add(i, (int)(buffer.next() & 0xFF));
			}
			
			this.supportsLongRange = BitUtils.isBit0(buffer.next());
		}
		


		@Override
		public String toString() {
			return "ApiStarted.Request [" + (wakeupReason != null ? "wakeupReason=" + wakeupReason + ", " : "")
					+ "watchdogStarted=" + watchdogStarted + ", deviceOptionMask=" + deviceOptionMask + ", "
					+ (genericDeviceType != null ? "genericDeviceType=" + genericDeviceType + ", " : "")
					+ (specificDeviceType != null ? "specificDeviceType=" + specificDeviceType + ", " : "")
					+ (commandClasses != null ? "commandClasses=" + commandClasses + ", " : "") + "supportsLongRange="
					+ supportsLongRange + "]";
		}
	}
}

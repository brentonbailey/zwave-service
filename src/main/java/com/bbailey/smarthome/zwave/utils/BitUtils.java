package com.bbailey.smarthome.zwave.utils;

public class BitUtils {

	public final static int BIT_7 = 0x80;
	public final static int BIT_6 = 0x40;
	public final static int BIT_5 = 0x20;
	public final static int BIT_4 = 0x10;
	public final static int BIT_3 = 0x08;
	public final static int BIT_2 = 0x04;
	public final static int BIT_1 = 0x02;
	public final static int BIT_0 = 0x01;
	
	public static int convertToInt16(int msb, int lsb) {
		return (msb << 8) | lsb;
	}
	
	public static int convertToInt24(int b1, int b2, int b3) {
		return (b1 << 16) | (b2 << 8) | b3;
	}
	
	public static int convertToInt32(int b1, int b2, int b3, int b4) {
		return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
	}
	
	
	public static String toHex(int value) {
		return String.format("0x%02X", (value & 0xFF));
	}
	
	public static boolean isBit7(int value) {
		return (value & BIT_7) > 0;
	}
	
	public static boolean isBit6(int value) {
		return (value & BIT_6) > 0;
	}
	
	public static boolean isBit5(int value) {
		return (value & BIT_5) > 0;
	}
	
	public static boolean isBit4(int value) {
		return (value & BIT_4) > 0;
	}
	
	public static boolean isBit3(int value) {
		return (value & BIT_3) > 0;
	}
	
	public static boolean isBit2(int value) {
		return (value & BIT_2) > 0;
	}
	
	public static boolean isBit1(int value) {
		return (value & BIT_1) > 0;
	}
	
	public static boolean isBit0(int value) {
		return (value & BIT_0) > 0;
	}
	
	public static boolean isBitN(int value, int bit) {
		switch (bit) {
		case 0:
			return isBit0(value);
		case 1:
			return isBit1(value);
		case 2:
			return isBit2(value);
		case 3:
			return isBit3(value);
		case 4:
			return isBit4(value);
		case 5:
			return isBit5(value);
		case 6:
			return isBit6(value);
		case 7:
			return isBit7(value);
		default:
			throw new IllegalArgumentException("Bit " + bit + " is not valid");
		}
	}
	
	
	public static int byte3(int value) {
		return (value >> 24) & 0xFF;
	}
	
	public static int byte2(int value) {
		return (value >> 16) & 0xFF;
	}
	
	public static int byte1(int value) {
		return (value >> 8) & 0xFF;
	}
	
	public static int byte0(int value) {
		return value & 0xFF;
	}
}

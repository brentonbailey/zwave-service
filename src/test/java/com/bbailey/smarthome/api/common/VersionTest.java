package com.bbailey.smarthome.api.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bbailey.smarthome.zwave.api.common.Version;
import com.bbailey.smarthome.zwave.utils.BitUtils;

public class VersionTest {

	@Test
	public void testFromString() {
		
		String str = "Z-Wave 7.15";
		Version v = new Version(str);
		assertEquals(7, v.getMajorVersion());
		assertEquals(15, v.getMinorVersion().orElse(-1));
	}
	
	
	@Test
	public void testToString() {
		Version v = new Version(7, 2, 3);
		assertEquals("7.2.3", v.toString());
	}
	
	
	@Test
	public void testCompare() {
		Version v1 = new Version(4);
		Version v2 = new Version(3,9);
		assertEquals(1, v1.compareTo(v2));
	}
}

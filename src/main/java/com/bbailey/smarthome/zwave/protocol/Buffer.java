package com.bbailey.smarthome.zwave.protocol;

import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;

import com.bbailey.smarthome.zwave.utils.BitUtils;

/**
 * Wrapper class for a byte array ensuring all of the casting from and to integers is handled
 */
public class Buffer {

	private final byte[] data;
	private int index = 0;
	
	
	/**
	 * Get the underlying array of bytes
	 * @return The underlying byte array
	 */
	public byte[] getData() {
		return data;
	}
	
	
	/**
	 * Get a value from the buffer
	 * @param idx The index
	 * @return The value at that index
	 */
	public int get(int idx) {
		return data[idx] & 0xFF;
	}
	
	
	/**
	 * Set a value in the buffer
	 * @param idx The index to set at
	 * @param value The value
	 */
	public void set(int idx, int value) {
		this.data[idx] = (byte)(value & 0xFF);
	}
	
	
	/**
	 * Get the next value from the buffer
	 * @return The next value in the buffer
	 */
	public int next() {
		return get(index++);
	}
	
	
	/**
	 * Get the next INT24 value from the buffer. This will
	 * consume 3 bytes
	 * @return The next INT 24
	 */
	public int nextInt24() {
		return BitUtils.convertToInt24(next(), next(), next());
	}
	
	
	/**
	 * Returns true if there is still data in the buffer
	 * @return True if there is data to read, false otherwise
	 */
	public boolean hasNext() {
		return index < data.length;
	}
	
	
	/**
	 * Get the length of the buffer
	 * @return The buffer length
	 */
	public int length() {
		return data.length;
	}
	
	public Buffer(byte[] data) {
		this.data = data;
	}
	
	public Buffer(int length) {
		this.data = new byte[length];
	}

	public Buffer() {
		this(0);
	}
	
	public static Buffer of(int... values) {
		Buffer buffer = new Buffer(values.length);
		for (int i = 0 ; i < values.length ; i++) {
			buffer.set(i, values[i]);
		}
		
		return buffer;
	}
	
	public static Buffer empty() {
		return new Buffer();
	}
	

	@Override
	public String toString() {
		String[] hex = new String[data.length];
		for (int i = 0 ; i < data.length ; i++) {
			hex[i] = BitUtils.toHex(get(i));
		}
		
		return "[" + Strings.join(Arrays.asList(hex), ',') + "]";
	}
	
	
}

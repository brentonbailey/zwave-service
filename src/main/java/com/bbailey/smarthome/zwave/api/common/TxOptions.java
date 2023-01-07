package com.bbailey.smarthome.zwave.api.common;

import com.bbailey.smarthome.zwave.utils.BitUtils;

public enum TxOptions {
	/**
	 * This option is used to request the destination node to return an MPDU acknowledgement. 
	 * This option SHOULD be used by a host application for all communication. 
	 * If the destination NodeID is the broadcast NodeID, the Z-Wave Module MUST ignore this bit.
	 */
	TRANSMIT_OPTION_ACK(BitUtils.BIT_0),
	/**
	 * This option is used to enable automatic routing. The Z-Wave library runs on the Z-Wave Module will try 
	 * transmitting the frame via repeater nodes in case destination node is out of direct range. 
	 * Controller nodes MAY use this bit to enable routing via Last Working Routes, calculated routes and routes 
	 * discovered via dynamic route resolution. End Nodes MAY use this bit to enable routing via return routes 
	 * for the actual destination nodeID (if any exist).If the destination is the broadcast NodeID, 
	 * the Z-Wave Module MUST ignore this option
	 */
	TRANSMIT_OPTION_AUTO_ROUTE(BitUtils.BIT_2),
	/**
	 * This option is used to explicitly disable any routing. This option MAY be used to force the 
	 * Z-Wave Module to send the frame without routing. All available routing information will be ignored. 
	 * This option SHOULD NOT be specified for normal application communication. 
	 * If the destination is the broadcast NodeID, the Z-Wave Module MUST ignore this option.
	 */
	TRANSMIT_OPTION_NO_ROUTE(BitUtils.BIT_4),
	/**
	 * This option is used to enable the usage of Explore NPDUs if needed. The transmit option TRANSMIT_OPTION_EXPLORE MAY 
	 * be used to enable dynamic route resolution. Dynamic route resolution allows a node to discover new routes 
	 * if all known routes are failing.
	 * 
	 * An Explore NPDU cannot wake up FLiRS nodes. An Explore NPDU uses normal RF power level minus 6dB. 
	 * This is also the power level used by a node finding its neighbors. For backwards compatibility reasons, 
	 * Z-Wave Module SHOULD ignore this option if the destination NodeID does not support Explore NDPUs.
	 */
	TRANSMIT_OPTION_EXPLORE(BitUtils.BIT_5);
	
	final int bitMask;
	
	public int getBitMask() {
		return bitMask;
	}
	
	private TxOptions(int bitMask) {
		this.bitMask = bitMask;
	}
}

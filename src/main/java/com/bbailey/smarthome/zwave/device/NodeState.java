package com.bbailey.smarthome.zwave.device;

public enum NodeState {
	/**
	 * The node is starting up
	 */
	INITIALISING,
	/**
	 * The node is awake and accepting requests
	 */
	AWAKE,
	/**
	 * The node is asleep so cannot process requests
	 */
	SLEEPING,
	/**
	 * The node is dead
	 */
	DEAD
}

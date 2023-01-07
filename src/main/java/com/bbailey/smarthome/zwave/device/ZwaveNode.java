package com.bbailey.smarthome.zwave.device;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.ZwaveAdapter;
import com.bbailey.smarthome.zwave.api.ApplicationCommandHandler;
import com.bbailey.smarthome.zwave.api.ApplicationUpdate;
import com.bbailey.smarthome.zwave.api.common.Command;
import com.bbailey.smarthome.zwave.commandclass.BasicCommandClass;
import com.bbailey.smarthome.zwave.commandclass.CommandClass;
import com.bbailey.smarthome.zwave.commandclass.WakeupCommandClass;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

public class ZwaveNode extends ZwaveDevice {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private NodeState state = NodeState.INITIALISING;
	
	private boolean alwaysListening = false;
	private boolean frequentlyListening = false;
	private boolean beaming = false;
	private int baudRate = 9600;
	
	private BasicDeviceType basicDeviceType;
	private GenericDeviceType genericDeviceType;
	private SpecificDeviceType specificDeviceType;
	
	private List<Integer> supportedCommandClasses;
	
	private Set<CommandClass> commandClasses = new HashSet<>();

	



	/**
	 * @return the alwaysListening
	 */
	public boolean isAlwaysListening() {
		return alwaysListening;
	}


	/**
	 * @param alwaysListening the alwaysListening to set
	 */
	public void setAlwaysListening(boolean alwaysListening) {
		this.alwaysListening = alwaysListening;
	}


	/**
	 * @return the frequentlyListening
	 */
	public boolean isFrequentlyListening() {
		return frequentlyListening;
	}


	/**
	 * @param frequentlyListening the frequentlyListening to set
	 */
	public void setFrequentlyListening(boolean frequentlyListening) {
		this.frequentlyListening = frequentlyListening;
	}


	/**
	 * @return the beaming
	 */
	public boolean isBeaming() {
		return beaming;
	}


	/**
	 * @param beaming the beaming to set
	 */
	public void setBeaming(boolean beaming) {
		this.beaming = beaming;
	}


	/**
	 * @return the baudRate
	 */
	public int getBaudRate() {
		return baudRate;
	}


	/**
	 * @param baudRate the baudRate to set
	 */
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}


	/**
	 * @return the basicDeviceType
	 */
	public BasicDeviceType getBasicDeviceType() {
		return basicDeviceType;
	}


	/**
	 * @param basicDeviceType the basicDeviceType to set
	 */
	public void setBasicDeviceType(BasicDeviceType basicDeviceType) {
		this.basicDeviceType = basicDeviceType;
	}


	/**
	 * @return the genericDeviceType
	 */
	public GenericDeviceType getGenericDeviceType() {
		return genericDeviceType;
	}


	/**
	 * @param genericDeviceType the genericDeviceType to set
	 */
	public void setGenericDeviceType(GenericDeviceType genericDeviceType) {
		this.genericDeviceType = genericDeviceType;
	}


	/**
	 * @return the specificDeviceType
	 */
	public SpecificDeviceType getSpecificDeviceType() {
		return specificDeviceType;
	}


	/**
	 * @param specificDeviceType the specificDeviceType to set
	 */
	public void setSpecificDeviceType(SpecificDeviceType specificDeviceType) {
		this.specificDeviceType = specificDeviceType;
	}


	/**
	 * @return the supportedCommandClasses
	 */
	public List<Integer> getSupportedCommandClasses() {
		return supportedCommandClasses;
	}


	/**
	 * @param supportedCommandClasses the supportedCommandClasses to set
	 */
	public void setSupportedCommandClasses(List<Integer> supportedCommandClasses) {
		this.supportedCommandClasses = supportedCommandClasses;
	}


	public ZwaveNode(ZwaveAdapter zwaveAdapter, int nodeId) {
		super(zwaveAdapter, String.format("node-%d", nodeId));
		setNodeId(nodeId);
		
		// Subscribe to ApplicationUdate callbacks
		zwaveAdapter.subscribe(
				ApplicationCommandHandler.Request.class, 
				this::handleCommand, 
				c -> c.getSourceNodeId() == getNodeId()
			);
	}
	
	
	/**
	 * Check whether a command class is supported by the device
	 * @param commandClassId The command class Id
	 * @return True if reported in the NIF, false otherwise
	 */
	public boolean isCommandClassSupported(int commandClassId) {
		return supportedCommandClasses.contains(commandClassId);
	}
	
	
	/**
	 * Add a command class implementation for this node
	 * @param commandClass
	 */
	public CommandClass addCommandClass(CommandClass commandClass) {
		
		if (commandClass != null) {
			LOGGER.info("Adding command class {} to node {}", BitUtils.toHex(commandClass.getCommandId()), getNodeId());
			commandClasses.add(commandClass);
		}
		
		return commandClass;
	}
	
	
	/**
	 * Checks whether the node implements the given command class
	 * @param commandClassId The command class Id
	 * @return True if there is an implementation, false otherwise
	 */
	public boolean isCommandClassImplemented(int commandClassId) {
		return commandClasses.stream().anyMatch(c -> c.getCommandId() == commandClassId);
	}
	

	
	
	private void handleCommand(ApplicationCommandHandler.Request request) {
		
		if (request.getSourceNodeId() != getNodeId()) {
			// This update is from a different node - ignore
			return;
		}
		
		Buffer payload = request.getPayload();
		int commandClassId = payload.next();
		CommandClass commandClass = getCommandClass(commandClassId);
		
		if (commandClass == null) {
			LOGGER.warn("Node does not have an implementation for command class {} - ignoring", BitUtils.toHex(commandClassId));
			return;
		}
		
		commandClass.receive(payload);
	}
	
	
	public CommandClass getCommandClass(int commandClassId) {
		CommandClass commandClass = commandClasses.stream()
				.filter(cc -> cc.getCommandId() == commandClassId)
				.findAny()
				.orElse(null);
		
		return commandClass;
	}	
}

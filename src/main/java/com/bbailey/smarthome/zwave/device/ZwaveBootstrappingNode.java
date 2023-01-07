package com.bbailey.smarthome.zwave.device;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.bbailey.smarthome.zwave.ZwaveAdapter;
import com.bbailey.smarthome.zwave.api.ApplicationUpdate;
import com.bbailey.smarthome.zwave.api.ApplicationUpdate.EventType;
import com.bbailey.smarthome.zwave.api.GetNodeInformationProtocolData;
import com.bbailey.smarthome.zwave.api.RequestNodeInformation;
import com.bbailey.smarthome.zwave.commandclass.AssociationCommandClass;
import com.bbailey.smarthome.zwave.commandclass.BasicCommandClass;
import com.bbailey.smarthome.zwave.commandclass.CommandClass;
import com.bbailey.smarthome.zwave.commandclass.CommandClassFactory;
import com.bbailey.smarthome.zwave.commandclass.VersionCommandClass;
import com.bbailey.smarthome.zwave.commandclass.VersionCommandClass.VersionListener;
import com.bbailey.smarthome.zwave.commandclass.VersionCommandClass.VersionedCommandClass;
import com.bbailey.smarthome.zwave.utils.BitUtils;


/**
 * This class represents a Z-Wave node with the extra logic
 * to bootstrap it for the first time. This will query the node
 * for various settings to discover all of the functionality
 * that is available 
 */
public class ZwaveBootstrappingNode extends ZwaveNode {

	
	
	public enum BootstrapStage {
		NODE_DATA,
		NIF,
		VERSIONS,
		ASSOCIATIONS
	}
	
	
	private Queue<BootstrapStage> remainingStages = new LinkedList<>();
	private Queue<Integer> undiscoveredCommandClasses = new LinkedList<>();
	
	private void progress() {
		
		BootstrapStage next = remainingStages.poll();
		if (next == null) {
			LOGGER.info("Node {} bootstrap complete", getNodeId());
			return;
		}
	
		switch (next) {
		case NODE_DATA:
			requestNodeData();
			break;
		case NIF:
			requestNif();
			break;
		case VERSIONS:
			requestVersions();
			break;
		case ASSOCIATIONS:
			requestAssociations();
			break;
		}
	}
	
	public ZwaveBootstrappingNode(ZwaveAdapter zwaveAdapter, int nodeId) {
		super(zwaveAdapter, nodeId);
		this.remainingStages.add(BootstrapStage.NODE_DATA);
		this.remainingStages.add(BootstrapStage.NIF);
		
		// Subscribe to the extra commands needed for Bootstrapping
		getAdapter().subscribe(GetNodeInformationProtocolData.Response.class, this::processNodeData);
		getAdapter().subscribe(
				ApplicationUpdate.Request.class, 
				this::processNif, 
				c -> c.getRemoteNodeId() == getNodeId()
			);
		
		
	}
	
	
	/**
	 * Start the bootstrapping process
	 */
	public void startBootstrapProcess() {
		LOGGER.info("Starting node {} bootstrap process", getNodeId());
		progress();
	}
	
	
	/**
	 * Request the protocol level properties for this node from the
	 * Z-Wave module
	 */
	private void requestNodeData() {
		LOGGER.info("Requesting protocol data for node {}", getNodeId());
		send(new GetNodeInformationProtocolData.Request(getNodeId()));
	}
	
	
	/**
	 * Request the Node Information Frame from the Z-Wave module
	 */
	private void requestNif() {
		LOGGER.info("Requesting NIF for node {}", getNodeId());
		send(new RequestNodeInformation.Request(getNodeId()));
	}
	
	
	/**
	 * Request the command class version supports from the
	 * Z-Wave modules
	 */
	private void requestVersions() {
		
		LOGGER.info("Requesting versions for node {}", getNodeId());
		
		VersionCommandClass versionCommandClass = (VersionCommandClass)addCommandClass(
				CommandClassFactory.create(this, VersionCommandClass.COMMAND_ID)
			);
		
		ZwaveNode node = this;
		versionCommandClass.addListener(new VersionListener() {

			@Override
			public void onProtocolVersionChange() {
				LOGGER.info("Version data discovered for node {}", getNodeId());
				requestNextCommandClass();
			}

			@Override
			public void onVersionedCommandClassAdded(VersionedCommandClass versionedCommandClass) {
				LOGGER.info("Discovered {} for node {}", versionedCommandClass, getNodeId());
				CommandClass commandClass = CommandClassFactory.create(node, versionedCommandClass);
				if (commandClass != null) {
					node.addCommandClass(commandClass);
				}
				requestNextCommandClass();
			}
			
			
		});
		// Get the protocol version
		versionCommandClass.sendGet();
	}
	
	
	private void requestNextCommandClass() {
		
		Integer commandClassId = undiscoveredCommandClasses.poll();
		if (commandClassId == null) {
			LOGGER.info("Discovered all command classes");
			progress();
			return;
		}
		
		VersionCommandClass versionCommandClass = (VersionCommandClass) getCommandClass(VersionCommandClass.COMMAND_ID);
		LOGGER.info("Requesting versions for node {} command class {}", getNodeId(), BitUtils.toHex(commandClassId));
		versionCommandClass.sendCommandClassGet(commandClassId);
	}
	
	/**
	 * Request association information from the Z-Wave module
	 */
	private void requestAssociations() {
		
	}
	
	
	/**
	 * Set up the protocol level properties for the node
	 * @param response The GetNodeInformationProtocolData response frame
	 */
	private void processNodeData(GetNodeInformationProtocolData.Response response) {
		
		// TODO: Filter to just this node
		
		LOGGER.info("Applying node information for node {}", getNodeId());
		setAlwaysListening(response.isListening());
		setFrequentlyListening(response.isSensor250ms() || response.isSensor1000ms());
		setBeaming(response.isBeamCapability());
		setBaudRate(response.getSupportedSpeed());
		setGenericDeviceType(response.getGenericDeviceClass());
		setBasicDeviceType(response.getBasicDeviceType());
		setSpecificDeviceType(response.getSpecificDeviceClass());
		
		progress();
	}
	
	
	/**
	 * Handle the Node Information Frame (NIF) to discover the command
	 * classes that this node supports
	 * @param request The NIF frame sent by the Zwave module
	 */
	private void processNif(ApplicationUpdate.Request request) {

		if (request.getEvent() != EventType.UPDATE_STATE_NODE_INFO_RECEIVED) {
			LOGGER.warn("{} - Failed to request NIF - {}", this, request.getEvent());
			return;
		}
		
		LOGGER.info("Applying NIF for node {}", getNodeId());
		
		List<Integer> supportedCommandClasses = request.getSupportedCommands();
		if (!supportedCommandClasses.contains(BasicCommandClass.COMMAND_ID)) {
			// All devices supported the BASIC command class
			supportedCommandClasses.add(BasicCommandClass.COMMAND_ID);
		}
		
		setSupportedCommandClasses(supportedCommandClasses);
		undiscoveredCommandClasses.addAll(supportedCommandClasses);
		
		if (supportedCommandClasses.contains(VersionCommandClass.COMMAND_ID)) {
			remainingStages.add(BootstrapStage.VERSIONS);
		} else {
			// Initialise the command classes on version 1
			LOGGER.info("COMMAND_CLASS_VERSION not supported, defaulting to version 1 for all command classes");
			for (int commandClassId : getSupportedCommandClasses()) {
				addCommandClass(CommandClassFactory.create(this, commandClassId));
			}
		}
		
		if (supportedCommandClasses.add(AssociationCommandClass.COMMAND_ID)) {
			remainingStages.add(BootstrapStage.ASSOCIATIONS);
		}
		
		// Move on to the next stage
		progress();
	}

}


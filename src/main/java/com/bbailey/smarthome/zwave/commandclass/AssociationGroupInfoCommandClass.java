package com.bbailey.smarthome.zwave.commandclass;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;

@CommandClassMeta(
		id = AssociationGroupInfoCommandClass.COMMAND_ID, 
		name = "COMMAND_CLASS_ASSOCIATION_GRP_INFO", 
		desc = "Command Class Association Group Info"
	)
public class AssociationGroupInfoCommandClass extends CommandClass {

	public final static Logger LOGGER = LoggerFactory.getLogger(AssociationGroupInfoCommandClass.class);
	
	public final static int COMMAND_ID = 0x59;
	
	private final static int ASSOCIATION_GROUP_NAME_GET = 0x01;
	private final static int ASSOCIATION_GROUP_NAME_REPORT = 0x02;
	private final static int ASSOCIATION_GROUP_INFO_GET = 0x03;
	private final static int ASSOCIATION_GROUP_INFO_REPORT = 0x04;
	private final static int ASSOCIATION_GROUP_COMMAND_LIST_GET = 0x05;
	private final static int ASSOCIATION_GROUP_COMMAND_LIST_REPORT = 0x06;
	
	private Map<Integer, String> associationGroups = new HashMap<>();
	
	public enum GroupInfoOption {
		LIST_MODE(0x40),
		REFRESH_CACHE(0x80);
		
		private final int bitmask;
		
		GroupInfoOption(int bitmask) {
			this.bitmask = bitmask;
		}
	}
	
	public AssociationGroupInfoCommandClass(int version, ZwaveNode node) {
		super(COMMAND_ID, version, node);
	}
	
	
	public void sendGetName(int groupId) {
		send(Buffer.of(COMMAND_ID, ASSOCIATION_GROUP_NAME_GET, groupId));
	}
	
	
	public void sendGetInfo(EnumSet<GroupInfoOption> options, int groupId) {
		int properties = 0x00;
		for (GroupInfoOption option : options) {
			properties = properties | option.bitmask;
		}
		
		send(Buffer.of(COMMAND_ID, ASSOCIATION_GROUP_INFO_GET, properties, groupId));
	}
	
	
	public void sendGetCommandList(boolean allowCache, int groupId) {
		int properties = allowCache ? 0x80 : 0x00;
		send(Buffer.of(COMMAND_ID, ASSOCIATION_GROUP_COMMAND_LIST_GET, properties, groupId));
	}
	
	
	@ReportHandler(id = ASSOCIATION_GROUP_NAME_REPORT)
	protected void handleGroupNameReport(Buffer buffer) {
		int groupId = buffer.next();
		int length = buffer.next();
		byte[] name = new byte[length];
		for (int i = 0 ; i < length ; i++) {
			name[i] = (byte)buffer.next();
		}
		
		associationGroups.put(groupId, new String(name));
	}
	
	
	@ReportHandler(id = ASSOCIATION_GROUP_INFO_REPORT)
	protected void handleGroupInfoReport(Buffer buffer) {
		
	}
	
	@ReportHandler(id = ASSOCIATION_GROUP_COMMAND_LIST_REPORT)
	protected void handleCommandListReport(Buffer buffer) {
		
		int groupId = buffer.next();
		int commandLength = buffer.next();
		for (int i = 0 ; i < commandLength ; i++) {
			int commandId = buffer.next();
		}
	}
}

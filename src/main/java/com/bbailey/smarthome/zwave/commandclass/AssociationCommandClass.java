package com.bbailey.smarthome.zwave.commandclass;

import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;

@CommandClassMeta(id = AssociationCommandClass.COMMAND_ID, name = "COMMAND_CLASS_ASSOCIATION", desc = "Command Class Association")
public class AssociationCommandClass extends CommandClass {

	public final static int COMMAND_ID = 0x85;
	
	// Version 1
	private final static int ASSOCIATION_SET = 0x01;
	private final static int ASSOCIATION_GET = 0x02;
	private final static int ASSOCIATION_REPORT = 0x03;
	private final static int ASSOCIATION_REMOVE = 0x04;
	private final static int ASSOCIATION_GROUPINGS_GET = 0x05;
	private final static int ASSOCIATION_GROUPINGS_REPORT = 0x06;
	
	// Version 2
	private final static int ASSOCIATION_SPECIFIC_GROUP_GET = 0x0B;
	private final static int ASSOCIATION_SPECIFIC_GROUP_REPORT = 0x0C;
	
	public AssociationCommandClass(int version, ZwaveNode node) {
		super(COMMAND_ID, version, node);
	}
	
	public void sendGet(int groupId) {
		send(Buffer.of(COMMAND_ID, ASSOCIATION_GET, groupId));
	}
	
	public void sendGetGroupings() {
		send(Buffer.of(COMMAND_ID, ASSOCIATION_GROUPINGS_GET));
	}
	
	public void sendRemove(int groupId, int nodeId) {
		send(Buffer.of(COMMAND_ID, ASSOCIATION_REMOVE, groupId, nodeId));
	}
	
	public void sendSet(int groupId, int nodeId) {
		send(Buffer.of(COMMAND_ID, ASSOCIATION_SET, groupId, nodeId));
	}
	
	public void sendSpecificGroupGet() {
		send(Buffer.of(COMMAND_ID, ASSOCIATION_SPECIFIC_GROUP_GET));
	}
	
	@ReportHandler(id = ASSOCIATION_REPORT)
	protected void handleReport(Buffer buffer) {
		
	}
	
	@ReportHandler(id = ASSOCIATION_GROUPINGS_REPORT)
	protected void handleGroupingsReport(Buffer buffer) {
		
	}
	
	@ReportHandler(id = ASSOCIATION_SPECIFIC_GROUP_REPORT)
	protected void handleSpecificGroupReport(Buffer buffer) {
		int group = buffer.next();
	}
	

}

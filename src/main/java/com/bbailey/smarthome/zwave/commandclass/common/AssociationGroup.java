package com.bbailey.smarthome.zwave.commandclass.common;

import java.util.List;
import java.util.Objects;

public class AssociationGroup {

	private final int groupId;
	private String name;
	private int maxNodes;
	
	private List<Integer> commandClasses;

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the maxNodes
	 */
	public int getMaxNodes() {
		return maxNodes;
	}

	/**
	 * @param maxNodes the maxNodes to set
	 */
	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

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

	public AssociationGroup(int groupId) {
		super();
		this.groupId = groupId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AssociationGroup)) {
			return false;
		}
		AssociationGroup other = (AssociationGroup) obj;
		return groupId == other.groupId;
	}

	@Override
	public String toString() {
		return "AssociationGroup [groupId=" + groupId + ", " + (name != null ? "name=" + name : "") + "]";
	}
	
}

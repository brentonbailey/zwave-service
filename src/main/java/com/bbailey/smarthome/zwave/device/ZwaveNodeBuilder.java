package com.bbailey.smarthome.zwave.device;

import java.util.List;

import com.bbailey.smarthome.zwave.ZwaveAdapter;
import com.bbailey.smarthome.zwave.api.common.LibraryType;
import com.bbailey.smarthome.zwave.api.common.Version;
import com.bbailey.smarthome.zwave.commandclass.VersionCommandClass.VersionedCommandClass;

public class ZwaveNodeBuilder {

	private ZwaveAdapter adapter;
	private int nodeId;
	private LibraryType libraryType;
	private Version protocolVersion;
	private Version applicationVersion;
	private List<Integer> supportedCommandClasses;
	private List<VersionedCommandClass> implementedCommandClasses;
}

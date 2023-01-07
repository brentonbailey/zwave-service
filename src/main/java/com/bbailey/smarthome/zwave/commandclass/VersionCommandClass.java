package com.bbailey.smarthome.zwave.commandclass;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbailey.smarthome.zwave.api.common.LibraryType;
import com.bbailey.smarthome.zwave.api.common.Version;
import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.protocol.Buffer;
import com.bbailey.smarthome.zwave.utils.BitUtils;

@CommandClassMeta(id = VersionCommandClass.COMMAND_ID, name = "COMMAND_CLASS_VERSION", desc = "Command Class Version")
public class VersionCommandClass extends CommandClass {

	private final static Logger LOGGER = LoggerFactory.getLogger(VersionCommandClass.class);
	
	public final static int COMMAND_ID = 0x86;
	
	public final static int VERSION_GET = 0x11;
	public final static int VERSION_REPORT = 0x12;
	public final static int VERSION_COMMAND_CLASS_GET = 0x13;
	public final static int VERSION_COMMAND_CLASS_REPORT = 0x14;
	
	// Version 3
	private final static int VERSION_CAPABILITIES_GET = 0x15;
	private final static int VERSION_CAPABILITIES_REPORT = 0x16;
	private final static int VERSION_ZWAVE_SOFTWARE_GET = 0x17;
	private final static int VERSION_ZWAVE_SOFTWARE_REPORT = 0x18;
	
	private LibraryType libraryType;
	private Version protocolVersion;
	private Version applicationVersion;
	
	private Set<VersionedCommandClass> versionedCommandClasses = new HashSet<>();
	
	private Set<VersionListener> listeners = new HashSet<>();
	
	
	/**
	 * @return the libraryType
	 */
	public LibraryType getLibraryType() {
		return libraryType;
	}


	/**
	 * @param libraryType the libraryType to set
	 */
	private void setLibraryType(LibraryType libraryType) {
		this.libraryType = libraryType;
	}
	
	
	/**
	 * @return the protocolVersion
	 */
	public Version getProtocolVersion() {
		return protocolVersion;
	}


	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	private void setProtocolVersion(Version protocolVersion) {
		this.protocolVersion = protocolVersion;
	}


	/**
	 * @return the applicationVersion
	 */
	public Version getApplicationVersion() {
		return applicationVersion;
	}


	/**
	 * @param applicationVersion the applicationVersion to set
	 */
	private void setApplicationVersion(Version applicationVersion) {
		this.applicationVersion = applicationVersion;
	}


	/**
	 * Get the versioned command class for a given command class Id
	 * @param commandClassId The command class Id
	 * @return The versioned command. If empty, you need to call get sendCommandClassGet to retrieve the required info
	 */
	public Optional<VersionedCommandClass> getVersionedCommandClass(int commandClassId) {
		return versionedCommandClasses.stream()
				.filter(v -> v.getCommandClassId() == commandClassId)
				.findAny();
	}


	public VersionCommandClass(int version, ZwaveNode node) {
		super(COMMAND_ID, version, node);
	}
	
	
	public void addListener(VersionListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(VersionListener listener) {
		listeners.remove(listener);
	}
	
	
	/**
	 * Send the request to get the modules library and protocol versions
	 */
	public void sendGet() {
		send(Buffer.of(COMMAND_ID, VERSION_GET));
	}
	
	
	/**
	 * Send the request to get the command class version info for the given
	 * command class
	 * @param commandClassId The command class to lookup version info for
	 */
	public void sendCommandClassGet(int commandClassId) {
		send(Buffer.of(COMMAND_ID, VERSION_COMMAND_CLASS_GET, commandClassId));
	}
	
	
	@ReportHandler(id = VERSION_REPORT)
	protected void handleReport(Buffer buffer) {
		setLibraryType(LibraryType.fromProtocol(buffer.next()));
		setProtocolVersion(new Version(buffer.next(), buffer.next()));
		setApplicationVersion(new Version(buffer.next(), buffer.next()));
		notifyListeners(null);
		LOGGER.info("Library Type: {}, protocol version: {}, application version {}", getLibraryType(), getProtocolVersion(), getApplicationVersion());
	}
	
	@ReportHandler(id = VERSION_COMMAND_CLASS_REPORT)
	protected void handleCommandClassReport(Buffer buffer) {
		int commandClassId = buffer.next();
		int version = buffer.next();
		
		VersionedCommandClass versionedCommandClass = new VersionedCommandClass(commandClassId, version);
		LOGGER.info("Versioned command class {} - {}", BitUtils.toHex(commandClassId), version);
		addVersionedCommandClass(versionedCommandClass);
	}
	
	
	private void addVersionedCommandClass(VersionedCommandClass versionedCommandClass) {
		boolean added = versionedCommandClasses.add(versionedCommandClass);
		if (added) {
			LOGGER.info("Discovered {}", versionedCommandClass);
			notifyListeners(versionedCommandClass);
		}
	}
	
	
	private void notifyListeners(VersionedCommandClass versionedCommandClass) {
		for (VersionListener listener : listeners) {
			if (versionedCommandClass == null) {
				listener.onProtocolVersionChange();
			} else {
				listener.onVersionedCommandClassAdded(versionedCommandClass);
			}
		}
	}
	
	
	public static class VersionedCommandClass {
		
		private final int commandClassId;
		private final int version;
		
		
		/**
		 * @return the commandClassId
		 */
		public int getCommandClassId() {
			return commandClassId;
		}
		
		
		/**
		 * @return the version
		 */
		public int getVersion() {
			return version;
		}


		public VersionedCommandClass(int commandClassId, int version) {
			this.commandClassId = commandClassId;
			this.version = version;
		}


		@Override
		public int hashCode() {
			return Objects.hash(commandClassId, version);
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VersionedCommandClass other = (VersionedCommandClass) obj;
			return commandClassId == other.commandClassId && version == other.version;
		}


		@Override
		public String toString() {
			return "VersionedCommandClass [commandClassId=" + BitUtils.toHex(commandClassId) + ", version=" + version + "]";
		}
	}
	
	
	public interface VersionListener {
		
		public void onProtocolVersionChange();
		
		public void onVersionedCommandClassAdded(VersionedCommandClass versionedCommandClass);
	}
}

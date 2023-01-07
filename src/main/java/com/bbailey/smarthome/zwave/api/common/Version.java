package com.bbailey.smarthome.zwave.api.common;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

	private final int majorVersion;
	private final Integer minorVersion;
	private final Integer patchVersion;
	
	
	/**
	 * @return the majorVersion
	 */
	public int getMajorVersion() {
		return majorVersion;
	}
	
	
	/**
	 * @return the minorVersion
	 */
	public Optional<Integer> getMinorVersion() {
		return Optional.ofNullable(minorVersion);
	}
	
	
	/**
	 * @return the patchVersion
	 */
	public Optional<Integer> getPatchVersion() {
		return Optional.ofNullable(patchVersion);
	}

	public Version(int majorVersion) {
		this(majorVersion, null, null);
	}
	
	public Version(int majorVersion, Integer minorVersion) {
		this(majorVersion, minorVersion, null);
	}
	

	public Version(int majorVersion, Integer minorVersion, Integer patchVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
	}
	
	
	public Version(String versionString) {
		
		Pattern p = Pattern.compile("(?<major>\\d+)(\\.(?<minor>\\d+))?(\\.(?<patch>\\d+))?");
		Matcher m = p.matcher(versionString);
		if (!m.find()) {
			throw new IllegalArgumentException(versionString + " is not a valid version string");
		}
		
		this.majorVersion = Integer.parseInt(m.group("major"));
		this.minorVersion = m.group("minor") != null ? Integer.parseInt(m.group("minor")) : null;
		this.patchVersion = m.group("patch") != null ? Integer.parseInt(m.group("patch")) : null;
	}
	
	
	@Override
	public int compareTo(Version o) {
		int c1 = Integer.compare(getMajorVersion(), o.getMajorVersion());
		if (c1 == 0) {
			int c2 = Integer.compare(getMinorVersion().orElse(0), o.getMinorVersion().orElse(0));
			if (c2 == 0) {
				return Integer.compare(getPatchVersion().orElse(0), o.getPatchVersion().orElse(0));
			}
			return c2;
		} 
		
		return c1;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(majorVersion, minorVersion, patchVersion);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Version)) {
			return false;
		}
		Version other = (Version) obj;
		return majorVersion == other.majorVersion && Objects.equals(minorVersion, other.minorVersion)
				&& Objects.equals(patchVersion, other.patchVersion);
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(majorVersion);
		if (minorVersion != null) {
			sb.append(".").append(minorVersion);
			if (patchVersion != null) {
				sb.append(".").append(patchVersion);
			}
		}
		
		return sb.toString();
	}
	
}

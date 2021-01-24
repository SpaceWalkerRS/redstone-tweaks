package redstonetweaks;

public class RedstoneTweaksVersion {
	
	public static final RedstoneTweaksVersion INVALID_VERSION = new RedstoneTweaksVersion(Type.INVALID, -1, -1, -1, -1);
	
	public final Type type;
	
	public final int major;
	public final int minor;
	public final int patch;
	
	public final int snapshot;
	
	private RedstoneTweaksVersion(Type type, int major, int minor, int patch, int snapshot) {
		this.type =  type;
		
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		
		this.snapshot = snapshot;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof RedstoneTweaksVersion) {
			RedstoneTweaksVersion version = (RedstoneTweaksVersion)other;
			
			return type == version.type && major == version.major && minor == version.minor && patch == version.patch && snapshot == version.snapshot;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return isValid() ? (major + "." + minor + "." + patch + (type == Type.SNAPSHOT ? "-pre" + snapshot : "")) : "INVALID";
	}
	
	public boolean isValid() {
		return this != INVALID_VERSION;
	}
	
	public boolean isNewerThan(RedstoneTweaksVersion version) {
		if (major == version.major) {
			if (minor == version.minor) {
				if (patch == version.patch) {
					if (type == version.type) {
						return type == Type.SNAPSHOT && snapshot > version.snapshot;
					}
					
					return type == Type.RELEASE;
				}
				
				return patch > version.patch;
			}
			
			return minor > version.minor;
		}
		
		return major > version.major;
	}
	
	public boolean isOlderThan(RedstoneTweaksVersion version) {
		return version.isNewerThan(this);
	}
	
	public static RedstoneTweaksVersion createRelease(int major, int minor, int patch) {
		return create(Type.RELEASE, major, minor, patch, 0);
	}
	
	public static RedstoneTweaksVersion createSnapshot(int major, int minor, int patch, int snapshot) {
		return create(Type.SNAPSHOT, major, minor, patch, snapshot);
	}
	
	private static RedstoneTweaksVersion create(Type type, int major, int minor, int patch, int snapshot) {
		if (type != Type.INVALID && major >= 0 && minor >= 0 && patch >= 0 && snapshot >= (type == Type.SNAPSHOT ? 1 : 0)) {
			return new RedstoneTweaksVersion(type, major, minor, patch, snapshot);
		}
		
		return INVALID_VERSION;
	}
	
	public static RedstoneTweaksVersion parseVersion(String string) {
		try {
			String[] args = string.split("-pre");
			String[] version = args[0].split("[.]");
			
			if (version.length == 3) {
				int major = Integer.parseInt(version[0]);
				int minor = Integer.parseInt(version[1]);
				int patch = Integer.parseInt(version[2]);
				
				if (args.length == 1) {
					return createRelease(major, minor, patch);
				} else if (args.length == 2) {
					return createSnapshot(major, minor, patch, Integer.parseInt(args[1]));
				}
			}
		} catch (Exception e) {
			
		}
		
		return INVALID_VERSION;
	}
	
	public enum Type {
		
		INVALID,
		RELEASE,
		SNAPSHOT;
		
	}
}

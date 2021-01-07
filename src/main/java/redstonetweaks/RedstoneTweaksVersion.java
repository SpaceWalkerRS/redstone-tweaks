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
	
	public static RedstoneTweaksVersion createRelease(int major, int minor, int patch) {
		return create(Type.RELEASE, major, minor, patch, 0);
	}
	
	public static RedstoneTweaksVersion createSnapshot(int major, int minor, int patch, int snapshot) {
		return create(Type.SNAPSHOT, major, minor, patch, snapshot);
	}
	
	public static RedstoneTweaksVersion create(Type type, int major, int minor, int patch, int snapshot) {
		if (type != Type.INVALID && major >= 0 && minor >= 0 && patch >= 0 && snapshot >= 0) {
			return new RedstoneTweaksVersion(type, major, minor, patch, snapshot);
		}
		return INVALID_VERSION;
	}
	
	public static RedstoneTweaksVersion parseVersion(String string) {
		Type type = Type.INVALID;
		int major = -1;
		int minor = -1;
		int patch = -1;
		int snapshot = -1;
		
		String[] args = string.split("-pre");
		
		switch (args.length) {
		case 1:
			type = Type.RELEASE;
			snapshot = 0;
			
			break;
		case 2:
			type = Type.SNAPSHOT;
			try {
				snapshot = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				
			}
			
			break;
		default:
			return INVALID_VERSION;
		}
		
		String[] version = args[0].split("[.]");
		
		if (version.length == 3) {
			try {
				major = Integer.parseInt(version[0]);
				minor = Integer.parseInt(version[1]);
				patch = Integer.parseInt(version[2]);
			} catch (NumberFormatException e) {
				
			}
		}
		
		return create(type, major, minor, patch, snapshot);
	}
	
	public enum Type {
		
		INVALID,
		RELEASE,
		SNAPSHOT;
		
	}
}

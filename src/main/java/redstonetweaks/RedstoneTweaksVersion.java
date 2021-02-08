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
		String version = String.format("%d.%d.%d", major, minor, patch);
		
		if (type == Type.SNAPSHOT) {
			version = String.format("%s-pre%d", version, snapshot);
		}
		
		return version;
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
	
	public static RedstoneTweaksVersion create(Type type, int major, int minor, int patch, int snapshot) {
		if (type != Type.INVALID && major >= 0 && minor >= 0 && patch >= 0 && snapshot >= (type == Type.SNAPSHOT ? 1 : 0)) {
			return new RedstoneTweaksVersion(type, major, minor, patch, snapshot);
		}
		
		return INVALID_VERSION;
	}
	
	public enum Type {
		
		INVALID(0),
		RELEASE(1),
		SNAPSHOT(2);
		
		private static final Type[] TYPES;
		
		static {
			TYPES = new Type[values().length];
			
			for (Type type : values()) {
				TYPES[type.index] = type;
			}
		}
		
		private final int index;
		
		private Type(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static Type fromIndex(int index) {
			if (index < 0 || index >= TYPES.length) {
				return INVALID;
			}
			
			return TYPES[index];
		}
	}
}

package redstonetweaks;

public class RedstoneTweaksVersion {
	
	public static final RedstoneTweaksVersion INVALID_VERSION = new RedstoneTweaksVersion(-1, -1, -1);
	
	public final int major;
	public final int minor;
	public final int patch;
	
	public RedstoneTweaksVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof RedstoneTweaksVersion) {
			RedstoneTweaksVersion version = (RedstoneTweaksVersion)other;
			return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return major + "." + minor + "." + patch;
	}
	
	public boolean isValid() {
		return !this.equals(INVALID_VERSION);
	}
	
	public static RedstoneTweaksVersion create(int major, int minor, int patch) {
		if (major >= 0 && minor >= 0 && patch >= 0) {
			return new RedstoneTweaksVersion(major, minor, patch);
		}
		return INVALID_VERSION;
	}

	public static RedstoneTweaksVersion fromString(String str) {
		String[] args = str.split("[.]", 0);
		if (args.length == 3) {
			try {
				int major = Integer.parseInt(args[0]);
				int minor = Integer.parseInt(args[1]);
				int patch = Integer.parseInt(args[2]);
				
				if (major >= 0 && minor >= 0 && patch >= 0)
			        return new RedstoneTweaksVersion(major, minor, patch);
			} catch (NumberFormatException e) {
				
		    }
		}
		return INVALID_VERSION;
	}
}

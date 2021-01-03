package redstonetweaks;

import redstonetweaks.packet.types.ServerInfoPacket;

public class ServerInfo {
	
	private static RedstoneTweaksVersion modVersion = RedstoneTweaksVersion.INVALID_VERSION;
	
	public static void init() {
		modVersion = RedstoneTweaks.MOD_VERSION;
	}
	
	public static void clear() {
		modVersion = RedstoneTweaksVersion.INVALID_VERSION;
	}
	
	public static RedstoneTweaksVersion getModVersion() {
		return modVersion;
	}
	
	public static void updateFromPacket(ServerInfoPacket packet) {
		modVersion = packet.modVersion;
	}
}

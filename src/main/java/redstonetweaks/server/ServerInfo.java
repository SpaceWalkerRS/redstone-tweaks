package redstonetweaks.server;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.packet.types.ServerInfoPacket;

public class ServerInfo {
	
	private static RedstoneTweaksVersion modVersion = RedstoneTweaksVersion.INVALID_VERSION;
	
	public static void onServerStart() {
		modVersion = RedstoneTweaks.MOD_VERSION;
	}
	
	public static void onServerStop() {
		clear();
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

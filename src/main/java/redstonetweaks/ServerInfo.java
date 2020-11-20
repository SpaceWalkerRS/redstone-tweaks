package redstonetweaks;

import redstonetweaks.packet.ServerInfoPacket;

public class ServerInfo {
	
	private RedstoneTweaksVersion modVersion;
	
	public ServerInfo() {
		this(RedstoneTweaksVersion.INVALID_VERSION);
	}
	
	public ServerInfo(RedstoneTweaksVersion modVersion) {
		this.modVersion = modVersion;
	}
	
	public void clear() {
		modVersion = RedstoneTweaksVersion.INVALID_VERSION;
	}
	
	public RedstoneTweaksVersion getModVersion() {
		return modVersion;
	}
	
	public void updateFromPacket(ServerInfoPacket packet) {
		modVersion = packet.modVersion;
	}
}

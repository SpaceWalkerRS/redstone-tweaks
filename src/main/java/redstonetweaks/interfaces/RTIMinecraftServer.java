package redstonetweaks.interfaces;

import redstonetweaks.ServerInfo;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

public interface RTIMinecraftServer {
	
	public ServerInfo getServerInfo();
	
	public ServerPacketHandler getPacketHandler();
	
	public ServerSettingsManager getSettingsManager();
	
	public ServerWorldTickHandler getWorldTickHandler();
	
	public void broadcastChunkData();
	
}

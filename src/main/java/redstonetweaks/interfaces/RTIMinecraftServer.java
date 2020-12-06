package redstonetweaks.interfaces;

import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

public interface RTIMinecraftServer {
	
	public ServerPacketHandler getPacketHandler();
	
	public ServerSettingsManager getSettingsManager();
	
	public ServerWorldTickHandler getWorldTickHandler();
	
	public void broadcastChunkData();
	
}

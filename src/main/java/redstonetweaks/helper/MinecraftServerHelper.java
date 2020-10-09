package redstonetweaks.helper;

import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

public interface MinecraftServerHelper {
	
	public ServerSettingsManager getSettingsManager();
	
	public ServerPacketHandler getPacketHandler();
	
	public ServerWorldTickHandler getWorldTickHandler();
	
	public void broadcastChunkData();
	
}

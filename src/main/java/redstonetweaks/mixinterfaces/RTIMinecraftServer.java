package redstonetweaks.mixinterfaces;

import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.setting.preset.ServerPresetsManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

public interface RTIMinecraftServer {
	
	public ServerPacketHandler getPacketHandler();
	
	public ServerSettingsManager getSettingsManager();
	
	public ServerPresetsManager getPresetsManager();
	
	public ServerWorldTickHandler getWorldTickHandler();
	
	public void broadcastChunkData();
	
}

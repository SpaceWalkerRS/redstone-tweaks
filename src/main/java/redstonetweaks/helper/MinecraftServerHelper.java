package redstonetweaks.helper;

import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.world.server.ServerTickHandler;
import redstonetweaks.world.server.ServerWorldHandler;

public interface MinecraftServerHelper {
	
	public ServerSettingsManager getSettingsManager();
	
	public ServerPacketHandler getPacketHandler();
	
	public ServerTickHandler getTickHandler();
	
	public ServerWorldHandler getWorldHandler();
}

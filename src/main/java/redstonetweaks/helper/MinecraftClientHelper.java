package redstonetweaks.helper;

import redstonetweaks.packet.ClientPacketHandler;
import redstonetweaks.setting.ClientSettingsManager;
import redstonetweaks.world.client.ClientWorldHandler;
import redstonetweaks.world.client.NeighborUpdateVisualizer;
import redstonetweaks.world.client.TickInfoLabelRenderer;

public interface MinecraftClientHelper {
	
	public ClientSettingsManager getSettingsManager();
	
	public NeighborUpdateVisualizer getNeighborUpdateVisualizer();
	
	public ClientPacketHandler getPacketHandler();
	
	public ClientWorldHandler getWorldHandler();
	
	public TickInfoLabelRenderer getTickInfoLabelRenderer();
	
}

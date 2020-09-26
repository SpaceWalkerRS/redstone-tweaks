package redstonetweaks.helper;

import redstonetweaks.packet.ClientPacketHandler;
import redstonetweaks.settings.ClientSettingsManager;
import redstonetweaks.world.client.ClientWorldTickHandler;
import redstonetweaks.world.client.NeighborUpdateVisualizer;
import redstonetweaks.world.client.TickInfoLabelRenderer;

public interface MinecraftClientHelper {
	
	public ClientSettingsManager getSettingsManager();
	
	public NeighborUpdateVisualizer getNeighborUpdateVisualizer();
	
	public ClientPacketHandler getPacketHandler();
	
	public ClientWorldTickHandler getWorldTickHandler();
	
	public TickInfoLabelRenderer getTickInfoLabelRenderer();
	
}

package redstonetweaks.interfaces;

import redstonetweaks.packet.ClientPacketHandler;
import redstonetweaks.setting.ClientSettingsManager;
import redstonetweaks.world.client.ClientWorldTickHandler;
import redstonetweaks.world.client.NeighborUpdateVisualizer;
import redstonetweaks.world.client.TickInfoLabelRenderer;

public interface RTIMinecraftClient {
	
	public ClientSettingsManager getSettingsManager();
	
	public NeighborUpdateVisualizer getNeighborUpdateVisualizer();
	
	public ClientPacketHandler getPacketHandler();
	
	public ClientWorldTickHandler getWorldTickHandler();
	
	public TickInfoLabelRenderer getTickInfoLabelRenderer();
	
}

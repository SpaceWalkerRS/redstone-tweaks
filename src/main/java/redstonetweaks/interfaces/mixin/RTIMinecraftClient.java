package redstonetweaks.interfaces.mixin;

import redstonetweaks.hotkeys.HotkeysManager;
import redstonetweaks.packet.ClientPacketHandler;
import redstonetweaks.setting.ClientSettingsManager;
import redstonetweaks.setting.preset.ClientPresetsManager;
import redstonetweaks.world.client.ClientWorldTickHandler;
import redstonetweaks.world.client.NeighborUpdateVisualizer;
import redstonetweaks.world.client.TickInfoLabelRenderer;

public interface RTIMinecraftClient {
	
	public ClientPacketHandler getPacketHandler();
	
	public ClientSettingsManager getSettingsManager();
	
	public ClientPresetsManager getPresetsManager();
	
	public HotkeysManager getHotkeysManager();
	
	public ClientWorldTickHandler getWorldTickHandler();
	
	public NeighborUpdateVisualizer getNeighborUpdateVisualizer();
	
	public TickInfoLabelRenderer getTickInfoLabelRenderer();
	
	public int getCurrentFps();
	
}

package redstonetweaks.setting.preset;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.packet.PresetPacket;

public class ClientPresetsManager {
	
	private final MinecraftClient client;
	
	public ClientPresetsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void onPresetChanged(Preset preset) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			PresetPacket packet = new PresetPacket(preset);
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void onDisconnect() {
		Presets.toDefault();
	}
}

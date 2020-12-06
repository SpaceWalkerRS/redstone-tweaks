package redstonetweaks.setting.preset;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.packet.PresetPacket;
import redstonetweaks.packet.ReloadPresetsPacket;

public class ClientPresetsManager {
	
	private final MinecraftClient client;
	
	public ClientPresetsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void onDisconnect() {
		Presets.toDefault();
	}
	
	public void presetChanged(PresetEditor editor) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			PresetPacket packet = new PresetPacket(editor);
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void reloadPresets() {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			ReloadPresetsPacket packet = new ReloadPresetsPacket();
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void onPresetPacketReceived(PresetEditor editor) {
		notifyMenuScreenOfPresetChange(editor.getPreset());
	}
	
	public void onPresetsPacketReceived() {
		notifyMenuScreenOfPresetChange(null);
	}
	
	public void onReloadPresetsPacketReceived() {
		notifyMenuScreenOfPresetChange(null);
	}
	
	private void notifyMenuScreenOfPresetChange(Preset preset) {
		Screen screen = client.currentScreen;
		if (screen instanceof RTMenuScreen) {
			((RTMenuScreen) screen).onPresetChanged(preset);
		}
	}
}

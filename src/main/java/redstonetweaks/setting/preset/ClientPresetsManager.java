package redstonetweaks.setting.preset;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.packet.RemovePresetPacket;
import redstonetweaks.packet.PresetPacket;
import redstonetweaks.packet.ReloadPresetsPacket;
import redstonetweaks.packet.ApplyPresetPacket;
import redstonetweaks.packet.DuplicatePresetPacket;

public class ClientPresetsManager {
	
	private final MinecraftClient client;
	
	public ClientPresetsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void onDisconnect() {
		Presets.toDefault();
	}
	
	public void applyPreset(Preset preset) {
		ApplyPresetPacket packet = new ApplyPresetPacket(preset);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void savePreset(PresetEditor editor) {
		PresetPacket packet = new PresetPacket(editor);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		
		editor.discardChanges();
	}
	
	public void reloadPresets() {
		ReloadPresetsPacket packet = new ReloadPresetsPacket();
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void removePreset(Preset preset) {
		RemovePresetPacket packet = new RemovePresetPacket(preset);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void duplicatePreset(Preset preset) {
		DuplicatePresetPacket packet = new DuplicatePresetPacket(preset);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void onPresetPacketReceived(Preset preset) {
		notifyMenuScreenOfPresetChange(preset);
	}
	
	public void onPresetsPacketReceived() {
		notifyMenuScreenOfPresetChange(null);
	}
	
	public void onReloadPresetsPacketReceived() {
		notifyMenuScreenOfPresetChange(null);
	}
	
	public void onRemovePresetPacketReceived(Preset preset) {
		notifyMenuScreenOfPresetChange(preset);
	}
	
	public void onApplyPresetPacketReceived(Preset preset) {
		Screen screen = client.currentScreen;
		if (screen instanceof RTMenuScreen) {
			((RTMenuScreen)screen).onSettingChanged(null);
		}
	}
	
	private void notifyMenuScreenOfPresetChange(Preset preset) {
		Screen screen = client.currentScreen;
		if (screen instanceof RTMenuScreen) {
			((RTMenuScreen)screen).onPresetChanged(preset);
		}
	}
}
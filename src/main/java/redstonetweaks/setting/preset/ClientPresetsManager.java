package redstonetweaks.setting.preset;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.packet.types.ApplyPresetPacket;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.ReloadPresetsPacket;
import redstonetweaks.packet.types.RemovePresetPacket;

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
		if (client.isInSingleplayer()) {
			editor.trySaveChanges();
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new PresetPacket(editor));
			
			editor.discardChanges();
		}
	}
	
	public void reloadPresets() {
		if (client.isInSingleplayer()) {
			((RTIMinecraftServer)client.getServer()).getPresetsManager().reloadPresets();
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ReloadPresetsPacket());
		}
	}
	
	public void removePreset(Preset preset) {
		if (client.isInSingleplayer()) {
			Presets.remove(preset);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new RemovePresetPacket(preset));
		}
	}
}

package redstonetweaks.setting.preset;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.packet.types.ApplyPresetPacket;
import redstonetweaks.packet.types.DeletePresetForeverPacket;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.ReloadPresetsPacket;
import redstonetweaks.packet.types.DeletePresetPacket;

public class ClientPresetsManager {
	
	private final MinecraftClient client;
	
	public ClientPresetsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void applyPreset(Preset preset) {
		if (client.isInSingleplayer()) {
			((RTIMinecraftServer)client.getServer()).getSettingsManager().applyPreset(preset);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ApplyPresetPacket(preset));
		}
	}
	
	public void savePreset(PresetEditor editor) {
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(new PresetPacket(editor));
	}
	
	public void reloadPresets() {
		if (client.isInSingleplayer()) {
			((RTIMinecraftServer)client.getServer()).getPresetsManager().reloadPresets();
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ReloadPresetsPacket());
		}
	}
	
	public void deletePreset(Preset preset) {
		if (client.isInSingleplayer()) {
			Presets.delete(preset);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new DeletePresetPacket(preset));
		}
	}
	
	public void deletePresetForever(Preset preset) {
		if (client.isInSingleplayer()) {
			Presets.deleteForever(preset);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new DeletePresetForeverPacket(preset));
		}
	}
	
	public void onConnect() {
		
	}
	
	public void onDisconnect() {
		if (!client.isInSingleplayer()) {
			Presets.reset();
		}
	}
}

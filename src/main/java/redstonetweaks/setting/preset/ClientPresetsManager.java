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
	
	public void applyPreset(Preset preset) {
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ApplyPresetPacket(preset));
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
	
	public void onConnect() {
		if (!client.isInSingleplayer()) {
			Presets.reset();
		}
	}
	
	public void onDisconnect() {
		if (!client.isInSingleplayer()) {
			Presets.reset();
		}
	}
}

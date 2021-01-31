package redstonetweaks.setting.preset;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.packet.types.ApplyPresetPacket;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.ReloadPresetsPacket;
import redstonetweaks.packet.types.RemovePresetPacket;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.ServerConfig;

public class ClientPresetsManager {
	
	private final MinecraftClient client;
	
	public ClientPresetsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void onDisconnect() {
		Presets.toDefault();
	}
	
	public boolean canEditPresets() {
		return ServerInfo.getModVersion().isValid() && client.player.hasPermissionLevel(ServerConfig.Presets.EDIT_PERMISSION_LEVEL.get()) && ServerConfig.Presets.EDIT_GAME_MODES.get(client.interactionManager.getCurrentGameMode());
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
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ReloadPresetsPacket());
	}
	
	public void removePreset(Preset preset) {
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(new RemovePresetPacket(preset));
	}
}

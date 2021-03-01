package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.settings.Settings;

public class ApplyPresetPacket extends AbstractRedstoneTweaksPacket {
	
	private Preset preset;
	
	public ApplyPresetPacket() {
		
	}
	
	public ApplyPresetPacket(Preset preset) {
		this.preset = preset;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(preset.getId());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		preset = Presets.fromId(buffer.readInt());
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (preset != null && PermissionManager.canEditPresets(player)) {
			((RTIMinecraftServer)server).getSettingsManager().applyPreset(preset);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && preset != null) {
			Settings.applyPreset(preset);
		}
	}
}

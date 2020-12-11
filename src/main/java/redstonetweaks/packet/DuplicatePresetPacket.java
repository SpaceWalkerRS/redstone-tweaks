package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class DuplicatePresetPacket extends RedstoneTweaksPacket {
	
	private String name;
	
	public DuplicatePresetPacket() {
		
	}
	
	public DuplicatePresetPacket(Preset preset) {
		name = preset.getName();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(name);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		name = buffer.readString(MAX_STRING_LENGTH);
	}
	
	@Override
	public void execute(MinecraftServer server) {
		Preset preset = Presets.fromName(name);
		
		if (preset != null) {
			((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().duplicatePreset(preset);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}

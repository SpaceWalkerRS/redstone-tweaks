package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class RemovePresetPacket extends RedstoneTweaksPacket {
	
	private String name;
	
	public RemovePresetPacket() {
		
	}
	
	public RemovePresetPacket(Preset preset) {
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
			((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().removePreset(preset);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		Preset preset = Presets.fromName(name);
		
		if (preset != null) {
			Presets.remove(preset);
		}
		
		((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onRemovePresetPacketReceived(preset);
	}
}

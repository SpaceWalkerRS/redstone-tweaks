package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.mixinterfaces.RTIMinecraftClient;
import redstonetweaks.mixinterfaces.RTIMinecraftServer;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class ApplyPresetPacket extends RedstoneTweaksPacket {
	
	private String name;
	
	public ApplyPresetPacket() {
		
	}
	
	public ApplyPresetPacket(Preset preset) {
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
			((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().applyPreset(preset);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		Preset preset = Presets.fromName(name);
		
		if (preset != null) {
			preset.apply();
			
			((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onApplyPresetPacketReceived(preset);
		}
	}
}

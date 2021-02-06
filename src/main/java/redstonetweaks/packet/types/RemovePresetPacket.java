package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class RemovePresetPacket extends RedstoneTweaksPacket {
	
	private int id;
	
	public RemovePresetPacket() {
		
	}
	
	public RemovePresetPacket(Preset preset) {
		System.out.println(preset.getId());
		this.id = preset.getId();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(id);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		id = buffer.readInt();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		Preset preset = Presets.fromId(id);
		
		if (preset != null) {
			Presets.remove(preset);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			Preset preset = Presets.fromId(id);
			
			if (preset != null) {
				Presets.remove(preset);
			}
		}
	}
}

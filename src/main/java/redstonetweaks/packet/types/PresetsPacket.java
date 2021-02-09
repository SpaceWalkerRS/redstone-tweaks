package redstonetweaks.packet.types;

import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.PacketUtils;

public class PresetsPacket extends RedstoneTweaksPacket {
	
	private int presetsCount;
	private Preset[] presets;
	private boolean[] removed;
	
	public PresetsPacket() {
		
	}
	
	public PresetsPacket(Collection<Preset> presetsCollection) {
		presetsCount = presetsCollection.size();
		presets = new Preset[presetsCount];
		
		int index = 0;
		for (Preset preset : presetsCollection) {
			presets[index++] = preset;
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(presetsCount);
		
		for (Preset preset : presets) {
			buffer.writeInt(preset.getId());
			buffer.writeString(preset.getName());
			buffer.writeString(preset.getDescription());
			buffer.writeByte(preset.getMode().getIndex());
			buffer.writeBoolean(preset.isEditable());
			buffer.writeBoolean(!Presets.isActive(preset));
			
			preset.encode(buffer);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		presetsCount = buffer.readInt();
		
		presets = new Preset[presetsCount];
		removed = new boolean[presetsCount];
		
		for (int i = 0; i < presetsCount; i++) {
			int id = buffer.readInt();
			String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
			String description = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
			Preset.Mode mode = Preset.Mode.fromIndex(buffer.readByte());
			boolean editable = buffer.readBoolean();
			removed[i] = buffer.readBoolean();
			
			Preset preset = new Preset(id, name, editable, name, description, mode);
			presets[i] = preset;
			
			preset.decode(buffer);
		}
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			Presets.reset();
			
			for (int index = 0; index < presetsCount; index++) {
				Preset preset = presets[index];
				
				if (Presets.register(preset)) {
					if (removed[index]) {
						Presets.remove(preset);
					}
				} else {
					preset.remove();
				}
			}
		}
	}
}

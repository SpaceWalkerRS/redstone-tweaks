package redstonetweaks.packet.types;

import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.PacketUtils;

public class PresetsPacket extends AbstractRedstoneTweaksPacket {
	
	private int presetsCount;
	private Preset[] presets;
	private boolean[] removed;
	private PacketByteBuf data;
	
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
		}
		
		for (Preset preset : presets) {
			preset.encode(buffer);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		presetsCount = buffer.readInt();
		
		presets = new Preset[presetsCount];
		removed = new boolean[presetsCount];
		
		for (int index = 0; index < presetsCount; index++) {
			int id = buffer.readInt();
			String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
			String description = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
			Preset.Mode mode = Preset.Mode.fromIndex(buffer.readByte());
			boolean editable = buffer.readBoolean();
			
			removed[index] = buffer.readBoolean();
			presets[index] = new Preset(id, name, editable, name, description, mode);
		}
		
		data = new PacketByteBuf(buffer.readBytes(buffer.readableBytes()));
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			Presets.reset();
			
			for (int index = 0; index < presetsCount; index++) {
				Preset preset = presets[index];
				
				preset.decode(data);
				
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

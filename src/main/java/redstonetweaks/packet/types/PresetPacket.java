package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.PacketUtils;

public class PresetPacket extends RedstoneTweaksPacket {
	
	private PresetEditor editor;
	
	public PresetPacket() {
		
	}
	
	public PresetPacket(PresetEditor editor) {
		this.editor = editor;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(editor.getId());
		buffer.writeString(editor.getName());
		buffer.writeString(editor.getDescription());
		buffer.writeByte(editor.getMode().getIndex());
		
		editor.encode(buffer);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		int id = buffer.readInt();
		String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		String description = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		Preset.Mode mode = Preset.Mode.fromIndex(buffer.readByte());
		
		editor = Presets.editPreset(Presets.fromIdOrCreate(id, name, description, mode));
		
		editor.decode(buffer);
	}
	
	@Override
	public void execute(MinecraftServer server) {
		editor.trySaveChanges();
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			editor.trySaveChanges();
		}
	}
}

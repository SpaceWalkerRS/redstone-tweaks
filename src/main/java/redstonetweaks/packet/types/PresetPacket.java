package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.PacketUtils;

public class PresetPacket extends AbstractRedstoneTweaksPacket {
	
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
		buffer.writeBoolean(editor.isLocal());
		
		editor.encode(buffer);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		int id = getId(buffer.readInt());
		String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		String description = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		Preset.Mode mode = Preset.Mode.fromIndex(buffer.readByte());
		boolean local = buffer.readBoolean();
		
		editor = Presets.editPreset(Presets.fromIdOrCreate(id, name, description, mode, local));
		
		editor.setName(name);
		editor.setDescription(description);
		editor.setMode(mode);
		editor.setIsLocal(local);
		
		editor.decode(buffer);
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (PermissionManager.canEditPresets(player)) {
			editor.trySaveChanges();
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			editor.trySaveChanges();
		}
	}
	
	private int getId(int id) {
		return id >= 0 ? id : Preset.nextId();
	}
}

package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsCategory;

public class LockCategoryPacket extends RedstoneTweaksPacket {
	
	public SettingsCategory category;
	public boolean locked;
	
	public LockCategoryPacket() {
		
	}
	
	public LockCategoryPacket(SettingsCategory category) {
		this(category, category.isLocked());
	}
	
	public LockCategoryPacket(SettingsCategory category, boolean locked) {
		this.category = category;
		this.locked = locked;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(category.getName());
		buffer.writeBoolean(locked);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		category = Settings.getCategoryFromName(buffer.readString(MAX_STRING_LENGTH));
		locked = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (category != null) {
			category.setLocked(locked);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (category != null && !client.isInSingleplayer()) {
			category.setLocked(locked);
		}
	}
}

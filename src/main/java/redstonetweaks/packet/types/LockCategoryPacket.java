package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.util.PacketUtils;

public class LockCategoryPacket extends AbstractRedstoneTweaksPacket {
	
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
		category = Settings.getCategoryFromName(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		locked = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (category != null && PermissionManager.canManageSettings(player)) {
			category.setLocked(locked);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && category != null) {
			((RTIMinecraftClient)client).getSettingsManager().setCategoryLocked(category, locked);
		}
	}
}

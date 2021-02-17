package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.client.PermissionManager;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class LockSettingPacket extends AbstractRedstoneTweaksPacket {
	
	public ISetting setting;
	public boolean locked;
	
	public LockSettingPacket() {
		
	}
	
	public LockSettingPacket(ISetting setting) {
		this(setting, setting.isLocked());
	}
	
	public LockSettingPacket(ISetting setting, boolean locked) {
		this.setting = setting;
		this.locked = locked;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(setting.getId());
		buffer.writeBoolean(locked);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		locked = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (setting != null && PermissionManager.canManageSettings(player)) {
			setting.setLocked(locked);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && setting != null) {
			setting.setLocked(locked);
		}
	}
}

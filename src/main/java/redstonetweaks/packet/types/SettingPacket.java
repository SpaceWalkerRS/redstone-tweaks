package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class SettingPacket extends AbstractRedstoneTweaksPacket {
	
	private ISetting setting;
	private PacketByteBuf data;
	
	public SettingPacket() {
		
	}
	
	public SettingPacket(ISetting setting) {
		this.setting = setting;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(setting.getId());
		setting.encode(buffer);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		data = new PacketByteBuf(buffer.readBytes(buffer.readableBytes()));
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (setting != null && PermissionManager.canChangeSettings(player) && PermissionManager.canChangeSettings(player, setting.getPack().getCategory())) {
			setting.decode(data);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && setting != null) {
			((RTIMinecraftClient)client).getSettingsManager().decodeSetting(setting, data);
		}
	}
}

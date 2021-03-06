package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class ResetSettingPacket extends AbstractRedstoneTweaksPacket {
	
	public ISetting setting;
	
	public ResetSettingPacket() {
		
	}
	
	public ResetSettingPacket(ISetting setting) {
		this.setting = setting;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(setting.getId());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (setting != null && PermissionManager.canChangeSettings(player) && PermissionManager.canChangeSettings(player, setting.getPack().getCategory())) {
			((RTIMinecraftServer)server).getSettingsManager().resetSetting(setting);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && setting != null) {
			((RTIMinecraftClient)client).getSettingsManager().resetSetting(setting, true);
		}
	}
}

package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.client.PermissionManager;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.util.PacketUtils;

public class ResetPackPacket extends AbstractRedstoneTweaksPacket {
	
	private SettingsPack pack;
	
	public ResetPackPacket() {
		
	}
	
	public ResetPackPacket(SettingsPack pack) {
		this.pack = pack;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(pack == null ? "null" : pack.getId());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pack = Settings.getPackFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (pack != null && PermissionManager.canManageSettings(player)) {
			((RTIMinecraftServer)server).getSettingsManager().resetPack(pack);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && pack != null) {
			pack.resetAll();
		}
	}
}

package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsPack;

public class ResetPackPacket extends RedstoneTweaksPacket {
	
	public SettingsPack pack;
	
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
		pack = Settings.getPackFromId(buffer.readString(MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (pack != null) {
			((RTIMinecraftServer)server).getSettingsManager().resetPack(pack);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (pack != null && !client.isInSingleplayer()) {
			pack.resetAll();
		}
	}
}

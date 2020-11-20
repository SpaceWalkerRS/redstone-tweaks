package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class ResetSettingPacket extends RedstoneTweaksPacket {
	
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
		setting = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server) {
		setting.reset();
		
		((RTIMinecraftServer)server).getSettingsManager().onResetSettingPacketReceived(setting);
	}
	
	@Override
	public void execute(MinecraftClient client) {
		setting.reset();
		
		((RTIMinecraftClient)client).getSettingsManager().onResetSettingPacketReceived(setting);
	}
}

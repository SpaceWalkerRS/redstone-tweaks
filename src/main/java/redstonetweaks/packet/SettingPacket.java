package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class SettingPacket extends RedstoneTweaksPacket {
	
	public ISetting setting;
	public String value;
	
	public SettingPacket() {
		
	}
	
	public SettingPacket(ISetting setting) {
		this.setting = setting;
		this.value = setting.getAsText();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(setting.getId());
		buffer.writeString(value);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		setting = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
		value = buffer.readString(MAX_STRING_LENGTH);
	}
	
	@Override
	public void execute(MinecraftServer server) {
		setting.setFromText(value);
		
		((RTIMinecraftServer)server).getSettingsManager().onSettingPacketReceived(setting);
	}
	
	@Override
	public void execute(MinecraftClient client) {
		setting.setFromText(value);
		
		((RTIMinecraftClient)client).getSettingsManager().onSettingPacketReceived(setting);
	}
}

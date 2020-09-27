package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.settings.Settings;
import redstonetweaks.settings.types.ISetting;

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
		setting = Settings.getSettingFromId(buffer.readString());
		value = buffer.readString();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		setting.setFromText(value);
		
		((MinecraftServerHelper)server).getSettingsManager().onSettingPacketReceived(setting);
	}
	
	@Override
	public void execute(MinecraftClient client) {
		setting.setFromText(value);
		
		((MinecraftClientHelper)client).getSettingsManager().onSettingPacketReceived(setting);
	}
}

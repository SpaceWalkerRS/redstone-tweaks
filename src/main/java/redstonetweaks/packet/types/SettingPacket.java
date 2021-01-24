package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.Setting;

public class SettingPacket extends RedstoneTweaksPacket {
	
	public ISetting setting;
	public String value;
	
	public SettingPacket() {
		
	}
	
	public SettingPacket(ISetting setting) {
		this(setting, setting.getValueAsString());
	}
	
	public <T> SettingPacket(Setting<T> setting, T value) {
		this(setting, setting.valueToString(value));
	}
	
	public SettingPacket(ISetting setting, String value) {
		this.setting = setting;
		this.value = value;
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
		if (setting != null) {
			setting.setValueFromString(value);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (setting != null) {
			setting.setValueFromString(value);
		}
	}
}

package redstonetweaks.packet;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.types.ISetting;

public class SettingsPacket extends RedstoneTweaksPacket {
	
	public int count;
	public ISetting[] settings;
	public String[] values;
	
	public SettingsPacket() {
		
	}
	
	public SettingsPacket(SettingsCategory category) {
		List<ISetting> list = category == null ? Settings.ALL : category.getSettings();
		
		count = list.size();
		settings = new ISetting[count];
		values = new String[count];
		
		for (ISetting setting : list) {
			if (--count < 0) {
				break;
			}
			
			settings[count] = setting;
			values[count] = setting.getAsString();
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(count);
		
		for (int i = 0; i < count; i++) {
			buffer.writeString(settings[i].getId());
			buffer.writeString(values[i]);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		count = buffer.readInt();
		settings = new ISetting[count];
		values = new String[count];
		
		for (int i = 0; i < count; i++) {
			settings[i] = Settings.getSettingFromId(buffer.readString());
			values[i] = buffer.readString();
		}
	}
	
	@Override
	public void execute(MinecraftServer server) {

	}

	@Override
	public void execute(MinecraftClient client) {
		updateSettings();
		
		((RTIMinecraftClient)client).getSettingsManager().onSettingsPacketReceived();
	}
	
	private void updateSettings() {
		for (int i = 0; i < count; i++) {
			ISetting setting = settings[i];
			
			if (setting != null) {
				setting.setEnabled(true);
				setting.setFromString(values[i]);
			}
		}
	}
}

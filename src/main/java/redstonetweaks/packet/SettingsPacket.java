package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.settings.SettingsPack;
import redstonetweaks.settings.Settings;
import redstonetweaks.settings.types.ISetting;

public class SettingsPacket extends RedstoneTweaksPacket {
	
	public int size;
	public ISetting[] settings;
	public String[] values;
	
	public SettingsPacket() {
		
	}
	
	public SettingsPacket(int count) {
		size = count;
		settings = new ISetting[count];
		values = new String[count];
		
		for (SettingsPack pack : Settings.SETTINGS_PACKS) {
			for (ISetting setting : pack.getSettings()) {
				if (--count < 0) {
					break;
				}
				
				settings[count] = setting;
				values[count] = setting.getAsText();
			}
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(size);
		
		for (int i = 0; i < size; i++) {
			buffer.writeString(settings[i].getId());
			buffer.writeString(values[i]);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		size = buffer.readInt();
		settings = new ISetting[size];
		values = new String[size];
		
		for (int i = 0; i < size; i++) {
			settings[i] = Settings.getSettingFromId(buffer.readString());
			values[i] = buffer.readString();
		}
	}
	
	@Override
	public void execute(MinecraftServer server) {

	}

	@Override
	public void execute(MinecraftClient client) {
		for (int i = 0; i < size; i++) {
			settings[i].setFromText(values[i]);
		}
	}
}

package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.ISetting;

public class SettingsPacket extends RedstoneTweaksPacket {
	
	public int count;
	public ISetting[] settings;
	public String[] values;
	
	public SettingsPacket() {
		
	}
	
	public SettingsPacket(int count) {
		this.count = count;
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
		for (int i = 0; i < count; i++) {
			settings[i].setFromText(values[i]);
		}
		((RTIMinecraftClient)client).getSettingsManager().onSettingsPacketReceived();
	}
}

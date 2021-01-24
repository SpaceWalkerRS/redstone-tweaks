package redstonetweaks.packet.types;

import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class SettingsPacket extends RedstoneTweaksPacket {
	
	public int count;
	public ISetting[] settings;
	public String[] values;
	
	public SettingsPacket() {
		
	}
	
	public SettingsPacket(Collection<ISetting> collection) {
		count = collection.size();
		settings = new ISetting[count];
		values = new String[count];
		
		int index = 0;
		for (ISetting setting : collection) {
			settings[index] = setting;
			values[index] = setting.getAsString();
			index++;
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(count);
		
		for (int index = 0; index < count; index++) {
			buffer.writeString(settings[index].getId());
			buffer.writeString(values[index]);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		count = buffer.readInt();
		settings = new ISetting[count];
		values = new String[count];
		
		for (int index = 0; index < count; index++) {
			settings[index] = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
			values[index] = buffer.readString(MAX_STRING_LENGTH);
		}
	}
	
	@Override
	public void execute(MinecraftServer server) {

	}

	@Override
	public void execute(MinecraftClient client) {
		while (count >= 0) {
			count--;
			
			ISetting setting = settings[count];
			if (setting != null) {
				setting.setEnabled(true);
				setting.setFromString(values[count]);
			}
		}
		
		((RTIMinecraftClient)client).getSettingsManager().onSettingsPacketReceived();
	}
}

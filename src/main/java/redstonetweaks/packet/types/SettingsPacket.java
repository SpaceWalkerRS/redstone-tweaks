package redstonetweaks.packet.types;

import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class SettingsPacket extends AbstractRedstoneTweaksPacket {
	
	public int count;
	public ISetting[] settings;
	
	public SettingsPacket() {
		
	}
	
	public SettingsPacket(Collection<ISetting> collection) {
		count = collection.size();
		settings = new ISetting[count];
		
		int index = 0;
		for (ISetting setting : collection) {
			settings[index++] = setting;
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(count);
		
		for (ISetting setting : settings) {
			buffer.writeString(setting.getId());
			setting.encode(buffer);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		count = buffer.readInt();
		
		for (int i = 0; i < count; i++) {
			ISetting setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
			if (setting != null) {
				setting.setEnabled(true);
				setting.decode(buffer);
			}
		}
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {

	}

	@Override
	public void execute(MinecraftClient client) {
		
	}
}

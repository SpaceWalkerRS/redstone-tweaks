package redstonetweaks.packet.types;

import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class SettingsPacket extends AbstractRedstoneTweaksPacket {
	
	private int count;
	private ISetting[] settings;
	private PacketByteBuf data;
	
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
		}
		for (ISetting setting : settings) {
			setting.encode(buffer);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		count = buffer.readInt();
		
		settings = new ISetting[count];
		for (int i = 0; i < count; i++) {
			settings[i] = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		}
		
		data = new PacketByteBuf(buffer.readBytes(buffer.readableBytes()));
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (PermissionManager.canChangeSettings(player)) {
			for (ISetting setting : settings) {
				if (setting != null) {
					setting.setEnabled(true);
					setting.decode(data);
				}
			}
		}
		
		data.release();
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getSettingsManager().decodeSettings(settings, data);
		
		data.release();
	}
}

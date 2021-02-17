package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class SettingPacket extends AbstractRedstoneTweaksPacket {
	
	public ISetting setting;
	
	public SettingPacket() {
		
	}
	
	public SettingPacket(ISetting setting) {
		this.setting = setting;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(setting.getId());
		setting.encode(buffer);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		
		if (setting != null) {
			setting.decode(buffer);
		}
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}

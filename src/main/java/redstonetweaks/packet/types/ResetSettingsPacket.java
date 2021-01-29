package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsCategory;

public class ResetSettingsPacket extends RedstoneTweaksPacket {
	
	public SettingsCategory category;
	
	public ResetSettingsPacket() {
		
	}
	
	public ResetSettingsPacket(SettingsCategory category) {
		this.category = category;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(category == null ? "null" : category.getName());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		category = Settings.getCategoryFromName(buffer.readString(MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (category != null) {
			((RTIMinecraftServer)server).getSettingsManager().resetSettings(category);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (category != null && !client.isInSingleplayer()) {
			category.resetAll();
		}
	}
}

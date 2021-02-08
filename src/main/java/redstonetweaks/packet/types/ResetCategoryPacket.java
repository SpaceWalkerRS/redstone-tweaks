package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.util.PacketUtils;

public class ResetCategoryPacket extends RedstoneTweaksPacket {
	
	public SettingsCategory category;
	
	public ResetCategoryPacket() {
		
	}
	
	public ResetCategoryPacket(SettingsCategory category) {
		this.category = category;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(category.getName());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		category = Settings.getCategoryFromName(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (category != null) {
			((RTIMinecraftServer)server).getSettingsManager().resetCategory(category);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && category != null) {
			category.resetAll();
		}
	}
}

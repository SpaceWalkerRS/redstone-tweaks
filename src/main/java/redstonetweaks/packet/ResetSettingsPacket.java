package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
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
		category = Settings.getCategoryFromName(buffer.readString());
	}
	
	@Override
	public void execute(MinecraftServer server) {
		reset();
		
		((RTIMinecraftServer)server).getSettingsManager().onResetSettingsPacketReceived();
	}
	
	@Override
	public void execute(MinecraftClient client) {
		reset();
		
		((RTIMinecraftClient)client).getSettingsManager().onResetSettingsPacketReceived();
	}
	
	private void reset() {
		if (category == null) {
			Settings.resetAll();
		} else {
			category.resetAll();
		}
	}
}

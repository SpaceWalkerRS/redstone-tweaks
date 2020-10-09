package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.setting.Settings;

public class ResetSettingsPacket extends RedstoneTweaksPacket {
	
	public ResetSettingsPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void execute(MinecraftServer server) {
		Settings.reset();
		
		((MinecraftServerHelper)server).getSettingsManager().onResetSettingsPacketReceived();
	}
	
	@Override
	public void execute(MinecraftClient client) {
		Settings.reset();
		
		((MinecraftClientHelper)client).getSettingsManager().onResetSettingsPacketReceived();
	}
}

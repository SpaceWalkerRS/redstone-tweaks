package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.gui.RTMenuScreen;

public class OpenMenuPacket extends RedstoneTweaksPacket {
	
	public OpenMenuPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (client.currentScreen == null) {
			client.openScreen(new RTMenuScreen(client));
		}
	}
}

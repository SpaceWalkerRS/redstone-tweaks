package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.interfaces.mixin.RTIMinecraftClient;

public class OpenMenuPacket extends AbstractRedstoneTweaksPacket {
	
	public OpenMenuPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).openRedstoneTweaksMenu();
	}
}

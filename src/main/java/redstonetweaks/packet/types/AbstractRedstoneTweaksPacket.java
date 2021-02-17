package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractRedstoneTweaksPacket {
	
	public abstract void encode(PacketByteBuf buffer);
	
	public abstract void decode(PacketByteBuf buffer);
	
	public abstract void execute(MinecraftServer server, ServerPlayerEntity player);
	
	public abstract void execute(MinecraftClient client);
	
}

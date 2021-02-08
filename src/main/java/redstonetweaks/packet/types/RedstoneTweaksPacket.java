package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public abstract class RedstoneTweaksPacket {
	
	public abstract void encode(PacketByteBuf buffer);
	
	public abstract void decode(PacketByteBuf buffer);
	
	public abstract void execute(MinecraftServer server);
	
	public abstract void execute(MinecraftClient client);
	
}

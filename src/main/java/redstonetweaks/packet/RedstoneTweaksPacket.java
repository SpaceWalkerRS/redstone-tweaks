package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public abstract class RedstoneTweaksPacket {
	
	protected static final int MAX_STRING_LENGTH = 32767;
	
	public abstract void encode(PacketByteBuf buffer);
	
	public abstract void decode(PacketByteBuf buffer);
	
	public abstract void execute(MinecraftServer server);
	
	public abstract void execute(MinecraftClient client);
}

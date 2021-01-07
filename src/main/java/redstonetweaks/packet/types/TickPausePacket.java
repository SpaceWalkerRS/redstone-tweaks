package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.mixinterfaces.RTIMinecraftServer;

public class TickPausePacket extends RedstoneTweaksPacket {
	
	public static final boolean PAUSE = false;
	public static final boolean ADVANCE = true;
	
	public boolean event;
	
	public TickPausePacket() {
		
	}
	
	public TickPausePacket(boolean event) {
		this.event = event;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(event);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		event = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		((RTIMinecraftServer)server).getWorldTickHandler().onTickPausePacketReceived(this);
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}
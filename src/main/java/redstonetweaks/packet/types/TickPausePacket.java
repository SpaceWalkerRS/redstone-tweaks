package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;

public class TickPausePacket extends RedstoneTweaksPacket {
	
	public boolean pause;
	
	public TickPausePacket() {
		
	}
	
	public TickPausePacket(boolean pause) {
		this.pause = pause;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(pause);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pause = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		((RTIMinecraftServer)server).getWorldTickHandler().pauseWorldTicking(pause);
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}

package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.RTIMinecraftClient;

public class DoWorldTicksPacket extends RedstoneTweaksPacket {
	
	public boolean doWorldTicks;
	
	public DoWorldTicksPacket() {
		
	}
	
	public DoWorldTicksPacket(boolean doWorldTicks) {
		this.doWorldTicks = doWorldTicks;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(doWorldTicks);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		doWorldTicks = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().onDoWorldTicksPacketReceived(this);
	}
}

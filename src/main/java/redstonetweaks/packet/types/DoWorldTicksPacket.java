package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;

public class DoWorldTicksPacket extends AbstractRedstoneTweaksPacket {
	
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
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().onDoWorldTicksPacketReceived(this);
	}
}

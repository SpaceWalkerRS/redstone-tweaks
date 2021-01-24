package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.world.common.WorldTickHandler.Status;

public class TickStatusPacket extends RedstoneTweaksPacket {
	
	public Status status;
	
	public TickStatusPacket() {
		
	}
	
	public TickStatusPacket(Status status) {
		this.status = status;
	}

	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(status.getIndex());
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		status = Status.fromIndex(buffer.readByte());
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().onTickStatusPacketReceived(this);
	}

}

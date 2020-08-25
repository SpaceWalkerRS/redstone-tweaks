package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.world.common.WorldHandler.Status;

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
		((MinecraftClientHelper)client).getWorldHandler().onTickStatusPacketReceived(this);
	}

}

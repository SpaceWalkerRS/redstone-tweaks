package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import redstonetweaks.helper.MinecraftClientHelper;

public class TickBlockEntityPacket extends RedstoneTweaksPacket {
	
	public BlockPos pos;
	
	public TickBlockEntityPacket() {
		
	}
	
	public TickBlockEntityPacket(BlockPos pos) {
		this.pos = pos;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((MinecraftClientHelper)client).getWorldHandler().onTickBlockEntityPacketReveiced(this);
	}

}

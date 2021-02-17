package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;

public class TickBlockEntityPacket extends AbstractRedstoneTweaksPacket {
	
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
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().tickBlockEntity(pos);
	}
}

package redstonetweaks.packet;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import redstonetweaks.interfaces.RTIClientWorld;
import redstonetweaks.world.common.UnfinishedEvent;
import redstonetweaks.world.common.UnfinishedEvent.Source;

public class UnfinishedEventPacket extends RedstoneTweaksPacket {
	
	public Source source;
	public BlockPos pos;
	public Block block;
	public int type;
	
	public UnfinishedEventPacket() {
		
	}
	
	public UnfinishedEventPacket(UnfinishedEvent event) {
		this.source = event.source;
		this.pos = event.pos;
		this.block = event.block;
		this.type = event.type;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(source.getIndex());
		buffer.writeBlockPos(pos);
		buffer.writeVarInt(Registry.BLOCK.getRawId(block));
		buffer.writeInt(type);
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		source = Source.fromIndex(buffer.readInt());
		pos = buffer.readBlockPos();
		block = Registry.BLOCK.get(buffer.readVarInt());
		type = buffer.readInt();
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIClientWorld)client.world).getUnfinishedEventScheduler().onUnfinishedEventPacketReceived(this);
	}

}

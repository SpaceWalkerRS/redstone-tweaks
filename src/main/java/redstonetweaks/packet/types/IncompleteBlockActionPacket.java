package redstonetweaks.packet.types;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import redstonetweaks.mixinterfaces.RTIClientWorld;
import redstonetweaks.world.common.IncompleteBlockAction;

public class IncompleteBlockActionPacket extends RedstoneTweaksPacket {
	
	public BlockPos pos;
	public Block block;
	public int type;
	
	public IncompleteBlockActionPacket() {
		
	}
	
	public IncompleteBlockActionPacket(IncompleteBlockAction action) {
		this.pos = action.getPos();
		this.type = action.getType();
		this.block = action.getObject();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeByte(type);
		buffer.writeVarInt(Registry.BLOCK.getRawId(block));
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
		type = buffer.readByte();
		block = Registry.BLOCK.get(buffer.readVarInt());
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIClientWorld)client.world).getIncompleteActionScheduler().onIncompleteActionPacketReceived(this);
	}
}

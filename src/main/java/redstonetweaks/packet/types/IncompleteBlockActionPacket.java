package redstonetweaks.packet.types;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import redstonetweaks.interfaces.mixin.RTIClientWorld;
import redstonetweaks.world.common.IncompleteBlockAction;

public class IncompleteBlockActionPacket extends RedstoneTweaksPacket {
	
	public BlockPos pos;
	public int type;
	public Block block;
	
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
		buffer.writeInt(Registry.BLOCK.getRawId(block));
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
		type = buffer.readByte();
		block = Registry.BLOCK.get(buffer.readInt());
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIClientWorld)client.world).getIncompleteActionScheduler().scheduleBlockAction(pos, type, block);
	}
}

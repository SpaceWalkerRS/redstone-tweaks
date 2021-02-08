package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.world.common.NeighborUpdate;
import redstonetweaks.world.common.UpdateType;
import redstonetweaks.world.server.ScheduledNeighborUpdate;

public class NeighborUpdateVisualizerPacket extends RedstoneTweaksPacket {

	public UpdateType updateType = UpdateType.NONE;
	public BlockPos pos;
	public BlockPos notifierPos;
	public BlockPos sourcePos;
	
	public NeighborUpdateVisualizerPacket() {
		
	}
	
	public NeighborUpdateVisualizerPacket(ScheduledNeighborUpdate update) {
		if (update != null) {
			NeighborUpdate neighborUpdate = update.getNeighborUpdate();
			
			this.updateType = neighborUpdate.getType();
			this.pos = neighborUpdate.getUpdatePos();
			this.notifierPos = neighborUpdate.getNotifierPos();
			this.sourcePos = neighborUpdate.getSourcePos();
		}
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeShort(updateType.getIndex());
		if (updateType != UpdateType.NONE) {
			buffer.writeBlockPos(pos);
			buffer.writeBlockPos(notifierPos);
			buffer.writeBlockPos(sourcePos);		
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		updateType = UpdateType.fromIndex(buffer.readShort());
		if (updateType != UpdateType.NONE) {
			pos = buffer.readBlockPos();
			notifierPos = buffer.readBlockPos();
			sourcePos = buffer.readBlockPos();
		}
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getNeighborUpdateVisualizer().updateBoxPositions(pos, notifierPos, sourcePos, updateType);
	}
}

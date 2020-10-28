package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.world.server.ScheduledNeighborUpdate;
import redstonetweaks.world.server.ScheduledNeighborUpdate.UpdateType;

public class NeighborUpdateVisualizerPacket extends RedstoneTweaksPacket {

	public BlockPos pos;
	public BlockPos notifierPos;
	public BlockPos sourcePos;
	public Direction direction;
	public UpdateType updateType;
	
	public NeighborUpdateVisualizerPacket() {
		
	}
	
	public NeighborUpdateVisualizerPacket(ScheduledNeighborUpdate update, BlockPos sourcePos) {
		this.pos = update == null ? null : update.pos;
		this.notifierPos = update == null ? null : update.notifierPos;
		this.sourcePos = sourcePos;
		this.direction = update == null ? null : update.direction;
		this.updateType = update == null ? UpdateType.NONE : update.updateType;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeShort(updateType.getIndex());
		if (updateType != UpdateType.NONE) {
			buffer.writeBlockPos(pos);
		}
		boolean hasNotifierPos = notifierPos != null;
		buffer.writeBoolean(hasNotifierPos);
		if (hasNotifierPos) {
			buffer.writeBlockPos(notifierPos);		
		}
		boolean hasSourcePos = sourcePos != null;
		buffer.writeBoolean(hasSourcePos);
		if (hasSourcePos) {
			buffer.writeBlockPos(sourcePos);		
		}	
		boolean hasDirection = direction != null;
		buffer.writeBoolean(hasDirection);
		if (hasDirection) {
			buffer.writeByte(direction.getId());
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		updateType = UpdateType.fromIndex(buffer.readShort());
		pos = updateType == UpdateType.NONE ? null : buffer.readBlockPos();
		boolean hasNotifierPos = buffer.readBoolean();
		notifierPos = hasNotifierPos ? buffer.readBlockPos() : null;
		boolean hasSourcePos = buffer.readBoolean();
		sourcePos = hasSourcePos ? buffer.readBlockPos() : null;
		boolean hasDirection = buffer.readBoolean();
		direction = hasDirection ? Direction.byId(buffer.readByte()) : null;
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getNeighborUpdateVisualizer().updateBoxPositions(this);
	}
}

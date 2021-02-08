package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.util.RelativePos;

// A neighbor update object with positions relative to the source of the neighbor update
public class AbstractNeighborUpdate {
	
	private Mode mode;
	private RelativePos notifierPos;
	private RelativePos updatePos;
	
	private BlockPos hashPos;
	
	// FOR PACKET DECODING ONLY
	public AbstractNeighborUpdate() {
		
	}
	
	public AbstractNeighborUpdate(Mode mode, RelativePos notifierPos, RelativePos updatePos) {
		this.mode = mode;
		this.notifierPos = notifierPos;
		this.updatePos = updatePos;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof AbstractNeighborUpdate) {
			AbstractNeighborUpdate update = (AbstractNeighborUpdate)other;
			
			return update.mode == mode && update.notifierPos == notifierPos && update.updatePos == updatePos;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashPos == null ? 0 : hashPos.hashCode();
	}
	
	@Override
	public String toString() {
		return mode + ":" + notifierPos + ":" + updatePos;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(mode.getIndex());
		buffer.writeByte(notifierPos.getIndex());
		buffer.writeByte(updatePos.getIndex());
	}
	
	public void decode(PacketByteBuf buffer) {
		mode = Mode.fromIndex(buffer.readByte());
		notifierPos = RelativePos.fromIndex(buffer.readByte());
		updatePos = RelativePos.fromIndex(buffer.readByte());
	}
	
	public static AbstractNeighborUpdate parseRelativeNeighborUpdate(String string) {
		String[] args = string.split(":");
		int index = 0;
		
		Mode mode = Mode.valueOf(args[index++]);
		RelativePos notifierPos = RelativePos.valueOf(args[index++]);
		RelativePos updatePos = RelativePos.valueOf(args[index++]);
		
		return new AbstractNeighborUpdate(mode, notifierPos, updatePos);
	}
	
	public AbstractNeighborUpdate copy() {
		return new AbstractNeighborUpdate(mode, notifierPos, updatePos);
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public RelativePos getNotifierPos() {
		return notifierPos;
	}
	
	public void setNotifierPos(RelativePos notifierPos) {
		this.notifierPos = notifierPos;
	}
	
	public RelativePos getUpdatePos() {
		return updatePos;
	}
	
	public void setUpdatePos(RelativePos updatePos) {
		this.updatePos = updatePos;
	}
	
	public void setHashPos(BlockPos pos, Direction sourceFacing, int offsetX, int offsetY, int offsetZ) {
		hashPos = notifierPos.toBlockPos(pos, sourceFacing).add(offsetX, offsetY, offsetZ);
	}
	
	public BlockUpdate toBlockUpdate(World world, BlockPos pos, BlockPos source, Direction sourceFacing, Block sourceBlock) {
		BlockPos notifier = notifierPos.toBlockPos(pos, sourceFacing);
		BlockPos update = updatePos.toBlockPos(notifier, sourceFacing);
		
		return new BlockUpdate(update, notifier, source, sourceBlock);
	}
	
	public ComparatorUpdate toComparatorUpdate(World world, BlockPos pos, BlockPos source, Direction sourceFacing, Block sourceBlock) {
		Direction dir = updatePos.asDirection(sourceFacing);
		
		if (dir == null) {
			return null;
		}
		
		BlockPos notifier = notifierPos.toBlockPos(pos, sourceFacing);
		BlockPos update = updatePos.toBlockPos(notifier, sourceFacing);
		BlockState state = world.getBlockState(update);
		
		if (!state.isOf(Blocks.COMPARATOR)) {
			if (state.isSolidBlock(world, update)) {
				update = update.offset(dir);
				state = world.getBlockState(update);
				
				if (!state.isOf(Blocks.COMPARATOR)) {
					return null;
				}
			} else {
				return null;
			}
		}
		
		return new ComparatorUpdate(update, notifier, source, sourceBlock);
	}
	
	public ShapeUpdate toShapeUpdate(World world, BlockPos pos, BlockPos source, BlockState notifierState, int flags, int depth) {
		BlockPos notifier = notifierPos.toBlockPos(pos, null);
		BlockPos update = updatePos.toBlockPos(notifier, null);
		
		return new ShapeUpdate(update, notifier, source, notifierState, updatePos.asDirection(null).getOpposite(), flags, depth);
	}
	
	public enum Mode {
		SINGLE_UPDATE(0, "Single Update"),
		NEIGHBORS(1, "Neighbors"),
		NEIGHBORS_EXCEPT(2, "Neighbors Except");
		
		private static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		private final String name;
		
		private Mode(int index, String name) {
			this.index = index;
			this.name = name;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static Mode fromIndex(int index) {
			if (index < 0) {
				return MODES[MODES.length - 1];
			}
			if (index >= MODES.length) {
				return MODES[0];
			}
			
			return MODES[index];
		}
		
		public String getName() {
			return name;
		}
		
		public Mode next() {
			return fromIndex(index + 1);
		}
		
		public Mode previous() {
			return fromIndex(index - 1);
		}
	}
}

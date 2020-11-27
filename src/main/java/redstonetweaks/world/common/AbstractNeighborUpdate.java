package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.util.RelativePos;

// A neighbor update object with positions relative to the source of the neighbor update
public class AbstractNeighborUpdate {
	
	private Mode mode;
	private RelativePos updatePos;
	private RelativePos notifierPos;
	
	private BlockPos hashPos;
	
	public AbstractNeighborUpdate(Mode mode, RelativePos updatePos, RelativePos notifierPos) {
		this.mode = mode;
		this.updatePos = updatePos;
		this.notifierPos = notifierPos;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof AbstractNeighborUpdate) {
			AbstractNeighborUpdate update = (AbstractNeighborUpdate)other;
			
			return update.mode == mode && update.updatePos.equals(updatePos) && update.notifierPos.equals(notifierPos);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashPos == null ? 0 : hashPos.hashCode();
	}
	
	@Override
	public String toString() {
		return mode + ":" + updatePos + ":" + notifierPos;
	}
	
	public static AbstractNeighborUpdate parseRelativeNeighborUpdate(String string) {
		String[] args = string.split(":");
		int index = 0;
		
		return new AbstractNeighborUpdate(Mode.valueOf(args[index++]), RelativePos.valueOf(args[index++]), RelativePos.valueOf(args[index++]));
	}
	
	public AbstractNeighborUpdate copy() {
		return new AbstractNeighborUpdate(mode, updatePos, notifierPos);
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public RelativePos getUpdatePos() {
		return updatePos;
	}
	
	public void setUpdatePos(RelativePos updatePos) {
		this.updatePos = updatePos;
	}
	
	public RelativePos getNotifierPos() {
		return notifierPos;
	}
	
	public void setNotifierPos(RelativePos notifierPos) {
		this.notifierPos = notifierPos;
	}
	
	public void setHashPos(BlockPos pos, Direction sourceFacing, int offsetX, int offsetY, int offsetZ) {
		hashPos = notifierPos.toBlockPos(pos, sourceFacing).add(offsetX, offsetY, offsetZ);
	}
	
	public BlockUpdate toBlockUpdate(World world, BlockPos pos, BlockPos source, Direction sourceFacing, Block sourceBlock) {
		BlockPos notifier = notifierPos.toBlockPos(pos, sourceFacing);
		BlockPos update = updatePos.toBlockPos(notifier, sourceFacing);
		return new BlockUpdate(update, notifier, source, world.getBlockState(update), sourceBlock);
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
		return new ComparatorUpdate(update, notifier, source, state, sourceBlock);
	}
	
	public ShapeUpdate toShapeUpdate(World world, BlockPos pos, BlockPos source, BlockState notifierState, int flags, int depth) {
		BlockPos notifier = notifierPos.toBlockPos(pos, null);
		BlockPos update = updatePos.toBlockPos(notifier, null);
		return new ShapeUpdate(update, notifier, source, world.getBlockState(update), notifierState, updatePos.asDirection(null).getOpposite(), flags, depth);
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

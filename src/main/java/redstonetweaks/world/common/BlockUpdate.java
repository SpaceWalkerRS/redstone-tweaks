package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.util.RelativePos;

public class BlockUpdate {
	
	private static long idCounter;
	
	private long id;
	private Mode mode;
	private RelativePos notifier;
	private RelativePos update;
	
	private BlockPos notifierPos;
	private BlockPos updatePos;
	
	public BlockUpdate(Mode mode, RelativePos notifier, RelativePos update) {
		this.id = idCounter++;
		this.mode = mode;
		this.notifier = notifier;
		this.update = update;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BlockUpdate) {
			return id == ((BlockUpdate)other).id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return notifierPos == null ? (int)id : notifierPos.hashCode();
	}
	
	public BlockUpdate copy() {
		return new BlockUpdate(mode, notifier, update);
	}
	
	public void initNotifier(BlockPos sourcePos, Direction sourceFacing) {
		this.notifierPos = notifier.getPos(sourcePos, sourceFacing);
		this.updatePos = update.getPos(notifierPos, sourceFacing);
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public RelativePos getNotifierPos() {
		return notifier;
	}
	
	public void setNotifierPos(RelativePos notifier) {
		this.notifier = notifier;
	}
	
	public RelativePos getUpdatePos() {
		return update;
	}

	public void setUpdatePos(RelativePos update) {
		this.update = update;
	}
	
	public void set(Mode mode, RelativePos notifier, RelativePos update) {
		this.mode = mode;
		this.notifier = notifier;
		this.update = update;
	}
	
	public void applyOffset(int x, int y, int z) {
		if (notifierPos != null) {
			notifierPos = notifierPos.add(x, y, z);
		}
	}
	
	public void removeOffset(int x, int y, int z) {
		applyOffset(-x, -y, -z);
	}
	
	public void dispatch(World world, Block sourceBlock) {
		switch (mode) {
		case SINGLE_UPDATE:
			world.updateNeighbor(updatePos, sourceBlock, notifierPos);
			break;
		case NEIGHBORS:
			world.updateNeighborsAlways(notifierPos, sourceBlock);
			break;
		case NEIGHBORS_EXCEPT:
			WorldHelper.updateNeighborsExcept(world, notifierPos, sourceBlock, update);
			break;
		default:
			break;
		}
	}
	
	public enum Mode {
		SINGLE_UPDATE(0, "Single Update"),
		NEIGHBORS(1, "Neighbors"),
		NEIGHBORS_EXCEPT(2, "Neighbors Except");
		
		private static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode dir : values()) {
				MODES[dir.index] = dir;
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
			if (index >= 0 && index < MODES.length) {
				return MODES[index];
			}
			return SINGLE_UPDATE;
		}
		
		public String getName() {
			return name;
		}
		
		public Mode next() {
			return fromIndex(index + 1);
		}
	}
}

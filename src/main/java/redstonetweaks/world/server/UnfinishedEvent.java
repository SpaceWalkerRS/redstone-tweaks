package redstonetweaks.world.server;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class UnfinishedEvent {
	
	public final Source source;
	public final BlockPos pos;
	public final BlockState state;
	public final Block block;
	public final int type;
	
	public final double viewDistance;
	
	public UnfinishedEvent(Source source, BlockPos pos, BlockState state, int type, double viewDistance) {
		this.source = source;
		this.pos = pos;
		this.state = state;
		this.block = state.getBlock();
		this.type = type;
		this.viewDistance = viewDistance;
	}
	
	public UnfinishedEvent(Source source, BlockPos pos, BlockState state, int type) {
		this(source, pos, state, type, -1);
	}
	
	public enum Source {
		INVALID(0),
		BLOCK(1),
		BLOCK_ENTITY(2),
		ENTITY(3);
		
		private final int index;
		
		Source(int index) {
			this.index = index;
		}

		public static Source fromIndex(int index) {
			switch (index) {
			case 1:
				return BLOCK;
			case 2:
				return BLOCK_ENTITY;
			case 3:
				return ENTITY;
			default:
				return INVALID;
			}
		}

		public int getIndex() {
			return index;
		}
	}
}

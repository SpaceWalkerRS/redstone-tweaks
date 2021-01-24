package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(SandBlock.class)
public abstract class SandBlockMixin extends FallingBlock {
	
	public SandBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return Tweaks.RedSand.CONNECTS_TO_WIRE.get();
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.isOf(Blocks.RED_SAND) ? Tweaks.RedSand.POWER_WEAK.get() : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.isOf(Blocks.RED_SAND) ? Tweaks.RedSand.POWER_STRONG.get() : 0;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		
		if (state.isOf(Blocks.RED_SAND) && Tweaks.RedSand.POWER_STRONG.get() > 0) {
			updateNeighbors(world, pos);
		}
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock()) && state.isOf(Blocks.RED_SAND)) {
			if (Tweaks.RedSand.POWER_STRONG.get() > 0) {
				updateNeighbors(world, pos);
			}
		}
	}
	
	private void updateNeighbors(World world, BlockPos pos) {
		((RTIWorld)world).dispatchBlockUpdates(pos, null, (SandBlock)(Object)this, Tweaks.RedSand.BLOCK_UPDATE_ORDER.get());
	}
}

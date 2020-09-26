package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.RedstoneDiodeHelper;
import redstonetweaks.helper.ServerTickSchedulerHelper;
import redstonetweaks.settings.Settings;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements RedstoneDiodeHelper {
	
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	
	@ModifyConstant(method = "getUpdateDelayInternal", constant = @Constant(intValue = 2))
	private int onGetUpdateDelayInternalModify2(int oldValue, BlockState state) {
		return state.get(Properties.POWERED) ? Settings.Repeater.DELAY_FALLING_EDGE.get() : Settings.Repeater.DELAY_RISING_EDGE.get();
	}
	
	// To fix the chain bug without altering other behavior,
	// we identify if the chain bug is occuring
	@Override
	public boolean isInputBugOccurring(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(Properties.HORIZONTAL_FACING);
		BlockPos frontPos = pos.offset(facing.getOpposite());
		BlockState frontState = world.getBlockState(frontPos);
		Direction frontFacing = frontState.get(Properties.HORIZONTAL_FACING);

		if (facing != frontFacing) {
			return false;
		}
		if (!state.isOf(Blocks.REPEATER)) {
			return false;
		}
		if (state.get(Properties.POWERED)) {
			return false;
		}
		if (frontState.get(Properties.POWERED)) {
			return ((ServerTickSchedulerHelper)world.getBlockTickScheduler()).hasScheduledTickAtTime(frontPos, frontState.getBlock(), getUpdateDelayInternal(state));
		} else {
			return world.getBlockTickScheduler().isTicking(frontPos, frontState.getBlock());
		}
	}
}

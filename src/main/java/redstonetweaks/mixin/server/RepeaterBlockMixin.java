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
import redstonetweaks.mixinterfaces.RTIRedstoneDiode;
import redstonetweaks.mixinterfaces.RTIServerTickScheduler;
import redstonetweaks.setting.Tweaks;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements RTIRedstoneDiode {
	
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	
	@ModifyConstant(method = "getUpdateDelayInternal", constant = @Constant(intValue = 2))
	private int onGetUpdateDelayInternalModify2(int oldValue, BlockState state) {
		return state.get(Properties.POWERED) ? Tweaks.Repeater.DELAY_FALLING_EDGE.get() : Tweaks.Repeater.DELAY_RISING_EDGE.get();
	}
	
	// To fix the chain bug without altering other behavior, we identify if the chain bug is occurring
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
			return ((RTIServerTickScheduler)world.getBlockTickScheduler()).hasScheduledTickAtTime(frontPos, frontState.getBlock(), getUpdateDelayInternal(state));
		}
		
		return world.getBlockTickScheduler().isTicking(frontPos, frontState.getBlock());
	}
}

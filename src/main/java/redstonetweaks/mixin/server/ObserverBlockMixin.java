package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.TickPriority;

import redstonetweaks.setting.Settings;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin {
	
	@ModifyConstant(method = "scheduledTick", constant = @Constant(intValue = 2, ordinal = 2))
	private int scheduledTickObserverDelay(int oldDelay) {
		return (int)Settings.observerDelay.get();
	}
	
	// The scheduleTick method is only called from 
	// inside the getStateForNeighborUpdate method.
	// If the bug fix for MC-189954 (https://bugs.mojang.com/browse/MC-189954)
	// is enabled, we call the isTicking method rather than
	// the isScheduled method.
	// The isScheduled method will return true if the observer
	// is scheduled to tick at the current world time, while
	// the isTicking method will return false.
	// The second part of this bug fix is to change the tick priority
	// of the observer if it detects an observer facing away from it.
	// That way 4 tick observer clocks still work if the bug fix is enabled.
	@Overwrite
	private void scheduleTick(IWorld world, BlockPos pos) {
		if ((boolean)Settings.MC189954.get()) {
			if (!world.isClient() && !world.getBlockTickScheduler().isTicking(pos, (ObserverBlock)(Object)this)) {
				BlockState state = world.getBlockState(pos);
				BlockPos pos2 = pos.offset(state.get(ObserverBlock.FACING));
				BlockState state2 = world.getBlockState(pos2);
				if (state2.getBlock() == Blocks.OBSERVER && state2.get(ObserverBlock.FACING) != state.get(ObserverBlock.FACING)) {
					world.getBlockTickScheduler().schedule(pos, (ObserverBlock)(Object)this, (int)Settings.observerDelay.get(), TickPriority.LOW);
				} else {
					world.getBlockTickScheduler().schedule(pos, (ObserverBlock)(Object)this, (int)Settings.observerDelay.get());
				}
			}
		} else if (!world.isClient() && !world.getBlockTickScheduler().isScheduled(pos, (ObserverBlock)(Object)this)) {
			world.getBlockTickScheduler().schedule(pos, (ObserverBlock)(Object)this, (int)Settings.observerDelay.get());
		}
	}
}

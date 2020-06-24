package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.MC136566;
import static redstonetweaks.setting.Settings.MC137127;
import static redstonetweaks.setting.Settings.MC189954;
import static redstonetweaks.setting.Settings.observerDelay;
import static redstonetweaks.setting.Settings.observerSignal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin<T> extends FacingBlock {
	
	protected ObserverBlockMixin(Settings settings) {
		super(settings);
	}

	// To fix MC-136566 (https://bugs.mojang.com/browse/MC-136566)
	// and MC-137127 (https://bugs.mojang.com/browse/MC-137127)
	// we change the flags argument given to the setBlockState call.
	// Adding 1 to the flags makes sure neighboring blocks are updated,
	// fixing MC-136566. Adding 48 to the flags makes sure neighboring
	// observers are updated, fixing MC-137127.
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 18))
	private int onBlockAddedFlags(int oldFlags) {
		int flags = oldFlags;
		if (MC136566.get()) {
			flags += 1;
		}
		if (MC137127.get()) {
			flags += 48;
		}
		return flags;
	}
	
	@ModifyConstant(method = "scheduledTick", constant = @Constant(intValue = 2, ordinal = 2))
	private int scheduledTickObserverDelay(int oldDelay) {
		return observerDelay.get();
	}
	
	// The scheduleTick method is only called from 
	// inside the getStateForNeighborUpdate method.
	// If the bug fix for MC-189954 (https://bugs.mojang.com/browse/MC-189954)
	// is enabled, we call the isTicking method rather than
	// the isScheduled method.
	// The isScheduled method will return false if the observer
	// is scheduled to tick at the current world time, while
	// the isTicking method will return true.
	// The second part of this bug fix is to change the tick priority
	// of the observer if it detects an observer facing away from it.
	// That way 4 tick observer clocks still work if the bug fix is enabled.
	@Redirect(method = "scheduleTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;isScheduled(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;)Z"))
	private boolean onScheduleTickRedirectIsScheduled(TickScheduler<T> tickScheduler, BlockPos pos, T object) {
		if (MC189954.get()) {
			return tickScheduler.isTicking(pos, object);
		} else {
			return tickScheduler.isScheduled(pos, object);
		}
	}
	
	@Inject(method = "scheduleTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V", shift = Shift.BEFORE))
	private void onScheduleTickInjectBeforeSchedule(IWorld world, BlockPos pos, CallbackInfo ci) {
		BlockState state = world.getBlockState(pos);
		TickPriority tickPriority = TickPriority.NORMAL;
		if (MC189954.get()) {
			BlockPos pos2 = pos.offset(state.get(FACING));
			BlockState state2 = world.getBlockState(pos2);
			if (state2.getBlock() == Blocks.OBSERVER && state2.get(FACING) != state.get(FACING)) {
				tickPriority = TickPriority.LOW;
			}
		}
		world.getBlockTickScheduler().schedule(pos, state.getBlock(), observerDelay.get(), tickPriority);
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return observerSignal.get();
	}
}

package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

	@Shadow protected abstract void dispense(ServerWorld world, BlockPos pos);
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean neighborUpdateRedirectIsReceivingRedstonePower1(World world1, BlockPos posUp, BlockState state, World world, BlockPos pos) {
		return WorldHelper.isQCPowered(world, pos, false, getQC(state), randQC(state));
	}

	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		// replaced by the redirect below
	}

	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onNeighborUpdateRedirectSetBlockState0(World world, BlockPos pos, BlockState state, int flags) {
		// Droppers and dispensers usually schedule their own ticks before updating neighboring blocks.
		// However, in the case where they have 0 delay, and are thus instantaneous, this leads to item dupes.
		// Therefore we schedule the tick after the block state has been set in that case.
		
		int delay = getDelay(state);
		
		boolean scheduleFirst = (delay != 0 && Tweaks.Global.DELAY_MULTIPLIER.get() != 0);
		
		if (scheduleFirst) {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, getTickPriority(state));
		}
		
		world.setBlockState(pos, state, flags);
		
		if (!scheduleFirst) {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, getTickPriority(state));
		}
		
		return true;
	}

	@Inject(method = "scheduledTick", at = @At(value = "HEAD"), cancellable = true)
	private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!isLazy(state) && !WorldHelper.isPowered(world, pos, true, getQC(state), randQC(state))) {
			ci.cancel();
		}
	}
	
	private DirectionToBooleanSetting getQC(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Tweaks.Dispenser.QC : Tweaks.Dropper.QC;
	}
	
	private boolean randQC(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Tweaks.Dispenser.RANDOMIZE_QC.get() : Tweaks.Dropper.RANDOMIZE_QC.get();
	}
	
	private int getDelay(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Tweaks.Dispenser.DELAY.get() : Tweaks.Dropper.DELAY.get();
	}
	
	private boolean isLazy(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Tweaks.Dispenser.LAZY.get() : Tweaks.Dropper.LAZY.get();
	}
	
	private TickPriority getTickPriority(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Tweaks.Dispenser.TICK_PRIORITY.get() : Tweaks.Dropper.TICK_PRIORITY.get();
	}
}

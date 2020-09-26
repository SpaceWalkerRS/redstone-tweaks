package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.settings.Settings;
import redstonetweaks.settings.types.DirectionalBooleanSetting;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

	@Shadow protected abstract void dispense(ServerWorld world, BlockPos pos);
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean neighborUpdateRedirectIsReceivingRedstonePower1(World world1, BlockPos posUp, BlockState state, World world, BlockPos pos) {
		return WorldHelper.isQCPowered(world, pos, state, false, getQC(state), randQC(state));
	}

	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay, BlockState state, World world, BlockPos blockPos, Block sourceBlock, BlockPos fromPos, boolean notify) {
		int delay = getDelay(state);
		if (delay > 0) {
			tickScheduler.schedule(pos, block, delay, getTickPriority(state));
		}
	}

	@Inject(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onNeighborUpdateInjectAfterSetBlockState0(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		if (!world.isClient()) {
			if (getDelay(state) == 0) {
				dispense((ServerWorld)world, pos);
			}
		}
	}

	@Inject(method = "scheduledTick", at = @At(value = "HEAD"), cancellable = true)
	private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!isLazy(state) && !world.isReceivingRedstonePower(pos) && !WorldHelper.isQCPowered(world, pos, state, true, getQC(state), randQC(state))) {
			ci.cancel();
		}
	}
	
	private DirectionalBooleanSetting getQC(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Settings.Dispenser.QC : Settings.Dropper.QC;
	}
	
	private boolean randQC(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Settings.Dispenser.RANDOMIZE_QC.get() : Settings.Dropper.RANDOMIZE_QC.get();
	}
	
	private int getDelay(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Settings.Dispenser.DELAY.get() : Settings.Dropper.DELAY.get();
	}
	
	private boolean isLazy(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Settings.Dispenser.LAZY.get() : Settings.Dropper.LAZY.get();
	}
	
	private TickPriority getTickPriority(BlockState state) {
		return state.isOf(Blocks.DISPENSER) ? Settings.Dispenser.TICK_PRIORITY.get() : Settings.Dropper.TICK_PRIORITY.get();
	}
}

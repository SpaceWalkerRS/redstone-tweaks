package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

	@Shadow protected abstract void dispense(ServerWorld world, BlockPos pos);
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean neighborUpdateRedirectIsReceivingRedstonePower1(World world1, BlockPos posUp, BlockState state, World world, BlockPos pos) {
		return WorldHelper.isQCPowered(world, pos, state, false);
	}

	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay, BlockState state, World world, BlockPos blockPos, Block sourceBlock, BlockPos fromPos, boolean notify) {
		int delay = getDelay(state);
		if (delay > 0) {
			tickScheduler.schedule(pos, object, delay, getTickPriority(state));
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
		if (!BLOCK_TO_SETTINGS_PACK.get(state.getBlock()).get(LAZY) && !world.isReceivingRedstonePower(pos) && !WorldHelper.isQCPowered(world, pos, state, true)) {
			ci.cancel();
		}
	}
	
	private int getDelay(BlockState state) {
		return BLOCK_TO_SETTINGS_PACK.get(state.getBlock()).get(DELAY);
	}
	
	private TickPriority getTickPriority(BlockState state) {
		return BLOCK_TO_SETTINGS_PACK.get(state.getBlock()).get(TICK_PRIORITY);
	}
}

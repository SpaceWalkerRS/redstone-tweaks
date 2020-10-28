package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.Settings;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin {
	
	@Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetPlacementStateRedirectGetReceivedPower(World world, BlockPos pos, ItemPlacementContext ctx) {
		return WorldHelper.isPowered(world, pos, world.getBlockState(pos), false, Settings.RedstoneLamp.QC, Settings.RedstoneLamp.RANDOMIZE_QC.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return WorldHelper.isPowered(world, pos, state, false, Settings.RedstoneLamp.QC, Settings.RedstoneLamp.RANDOMIZE_QC.get());
	}
	
	@Inject(method = "neighborUpdate", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private void onNeighborUpdateInjectBeforeSchedule(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		int delay = Settings.RedstoneLamp.DELAY_FALLING_EDGE.get();
		if (delay == 0) {
			world.setBlockState(pos, state.cycle(Properties.LIT), 2);
		} else {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, Settings.RedstoneLamp.TICK_PRIORITY_FALLING_EDGE.get());
		}
		ci.cancel();
	}
	
	@Inject(method = "neighborUpdate", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onNeighborUpdateInjectBeforeSetBlockState(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		int delay = Settings.RedstoneLamp.DELAY_RISING_EDGE.get();
		if (delay > 0) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, Settings.RedstoneLamp.TICK_PRIORITY_RISING_EDGE.get());
			ci.cancel();
		}
	}
	
	@Inject(method = "scheduledTick", at = @At(value = "HEAD"), cancellable = true)
	private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		boolean powered = state.get(Properties.LIT);
		boolean isReceivingPower = WorldHelper.isPowered(world, pos, state, true, Settings.RedstoneLamp.QC, Settings.RedstoneLamp.RANDOMIZE_QC.get());
		boolean shouldBePowered = isLazy(powered) ? !powered : isReceivingPower;

		if (powered != shouldBePowered) {
			world.setBlockState(pos, state.cycle(Properties.LIT), 2);
			if (shouldBePowered != isReceivingPower) {
				world.updateNeighbor(pos, state.getBlock(), pos);
			}
		}
		ci.cancel();
	}

	private boolean isLazy(boolean currentlyPowered) {
		return currentlyPowered ? Settings.RedstoneLamp.LAZY_FALLING_EDGE.get() : Settings.RedstoneLamp.LAZY_RISING_EDGE.get();
	}
}

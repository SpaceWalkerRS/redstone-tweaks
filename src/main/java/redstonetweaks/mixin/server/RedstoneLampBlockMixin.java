package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin {
	
	@Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetPlacementStateRedirectGetReceivedPower(World world, BlockPos pos, ItemPlacementContext ctx) {
		return WorldHelper.isPowered(world, pos, false, Tweaks.RedstoneLamp.QC, Tweaks.RedstoneLamp.RANDOMIZE_QC.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return WorldHelper.isPowered(world, pos, false, Tweaks.RedstoneLamp.QC, Tweaks.RedstoneLamp.RANDOMIZE_QC.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos blockPos, T obj, int oldDelay, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.RedstoneLamp.DELAY_FALLING_EDGE.get(), Tweaks.RedstoneLamp.TICK_PRIORITY_FALLING_EDGE.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onNeighborUpdateRedirectSetBlockState(World world1, BlockPos pos1, BlockState newState, int flags, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.RedstoneLamp.DELAY_RISING_EDGE.get(), Tweaks.RedstoneLamp.TICK_PRIORITY_RISING_EDGE.get());
		
		return true;
	}
	
	@Inject(method = "scheduledTick", at = @At(value = "HEAD"), cancellable = true)
	private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		boolean powered = state.get(Properties.LIT);
		boolean isReceivingPower = WorldHelper.isPowered(world, pos, true, Tweaks.RedstoneLamp.QC, Tweaks.RedstoneLamp.RANDOMIZE_QC.get());
		boolean shouldBePowered = isLazy(powered) ? !powered : isReceivingPower;

		if (powered != shouldBePowered) {
			world.setBlockState(pos, state.cycle(Properties.LIT), 2);
			
			if (shouldBePowered != isReceivingPower) {
				world.updateNeighbor(pos, state.getBlock(), pos);
			}
		} else if (Tweaks.Global.SPONTANEOUS_EXPLOSIONS.get()) {
			WorldHelper.createSpontaneousExplosion(world, pos);
		}
		
		ci.cancel();
	}

	private boolean isLazy(boolean currentlyPowered) {
		return currentlyPowered ? Tweaks.RedstoneLamp.LAZY_FALLING_EDGE.get() : Tweaks.RedstoneLamp.LAZY_RISING_EDGE.get();
	}
}

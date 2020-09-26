package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin extends AbstractBlock {

	public PoweredRailBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance);

	@ModifyConstant(method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;ZI)Z", constant = @Constant(intValue = 8))
	private int getPoweredRailLimit(int oldValue, World world, BlockPos pos, BlockState state, boolean bl, int distance) {
		int limit = state.isOf(Blocks.ACTIVATOR_RAIL) ? redstonetweaks.settings.Settings.ActivatorRail.POWER_LIMIT.get() : redstonetweaks.settings.Settings.PoweredRail.POWER_LIMIT.get() ;
		return limit - 1;
	}

	@Inject(method = "updateBlockState", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onUpdateBlockStateInjectBeforeSetBlockState(BlockState state, World world, BlockPos pos, Block neighbor, CallbackInfo ci, boolean powered) {
		if (world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			ci.cancel();
		} else {
			int delay = getDelay(state, powered);
			if (delay > 0) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, getTickPriority(state, powered));
				ci.cancel();
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean powered = state.get(Properties.POWERED);
		boolean shouldBePowered = isLazy(state, powered) ? !powered : isReceivingPower(world, pos, state);

		if (powered != shouldBePowered) {
			world.setBlockState(pos, state.with(Properties.POWERED, shouldBePowered), 3);
			world.updateNeighborsAlways(pos.down(), state.getBlock());
			if ((state.get(Properties.STRAIGHT_RAIL_SHAPE)).isAscending()) {
				world.updateNeighborsAlways(pos.up(), state.getBlock());
			}
		}
	}

	private boolean isReceivingPower(ServerWorld world, BlockPos pos, BlockState state) {
		return world.isReceivingRedstonePower(pos) || isPoweredByOtherRails(world, pos, state, true, 0) || isPoweredByOtherRails(world, pos, state, false, 0);
	}

	private int getDelay(BlockState state, boolean currentlyPowered) {
		if (state.isOf(Blocks.ACTIVATOR_RAIL)) {
			return currentlyPowered ? redstonetweaks.settings.Settings.ActivatorRail.DELAY_FALLING_EDGE.get() : redstonetweaks.settings.Settings.ActivatorRail.DELAY_RISING_EDGE.get();
		} else {
			return currentlyPowered ? redstonetweaks.settings.Settings.PoweredRail.DELAY_FALLING_EDGE.get() : redstonetweaks.settings.Settings.PoweredRail.DELAY_RISING_EDGE.get();
		}
	}

	private TickPriority getTickPriority(BlockState state, boolean currentlyPowered) {
		if (state.isOf(Blocks.ACTIVATOR_RAIL)) {
			return currentlyPowered ? redstonetweaks.settings.Settings.ActivatorRail.TICK_PRIORITY_FALLING_EDGE.get() : redstonetweaks.settings.Settings.ActivatorRail.TICK_PRIORITY_RISING_EDGE.get();
		} else {
			return currentlyPowered ? redstonetweaks.settings.Settings.PoweredRail.TICK_PRIORITY_FALLING_EDGE.get() : redstonetweaks.settings.Settings.PoweredRail.TICK_PRIORITY_RISING_EDGE.get();
		}
	}

	private boolean isLazy(BlockState state, boolean currentlyPowered) {
		if (state.isOf(Blocks.ACTIVATOR_RAIL)) {
			return currentlyPowered ? redstonetweaks.settings.Settings.ActivatorRail.LAZY_FALLING_EDGE.get() : redstonetweaks.settings.Settings.ActivatorRail.LAZY_RISING_EDGE.get();
		} else {
			return currentlyPowered ? redstonetweaks.settings.Settings.PoweredRail.LAZY_FALLING_EDGE.get() : redstonetweaks.settings.Settings.PoweredRail.LAZY_RISING_EDGE.get();
		}
	}
}

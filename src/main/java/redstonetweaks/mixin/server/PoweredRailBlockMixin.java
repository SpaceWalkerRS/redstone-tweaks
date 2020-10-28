package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.types.DirectionalBooleanSetting;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin extends AbstractBlock {

	public PoweredRailBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow protected abstract boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance);

	@ModifyConstant(method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;ZI)Z", constant = @Constant(intValue = 8))
	private int getPoweredRailLimit(int oldValue, World world, BlockPos pos, BlockState state, boolean bl, int distance) {
		int limit = state.isOf(Blocks.ACTIVATOR_RAIL) ? redstonetweaks.setting.Settings.ActivatorRail.POWER_LIMIT.get() : redstonetweaks.setting.Settings.PoweredRail.POWER_LIMIT.get() ;
		return limit - 1;
	}
	
	@Redirect(method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ZILnet/minecraft/block/enums/RailShape;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onisPoweredByOtherRailsRedirectGetReceivedPower(World world1, BlockPos blockPos, World world, BlockPos pos, boolean bl, int distance, RailShape shape) {
		BlockState state = world.getBlockState(pos);
		return WorldHelper.isPowered(world, pos, state, false, getQC(state), randQC(state));
	}
	
	@Redirect(method = "updateBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onUpdateBlockStateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block neighbor) {
		return WorldHelper.isPowered(world, pos, state, false, getQC(state), randQC(state));
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
		boolean shouldBePowered = isLazy(state, powered) ? !powered : isPowered(world, pos, state, true);

		if (powered != shouldBePowered) {
			world.setBlockState(pos, state.with(Properties.POWERED, shouldBePowered), 3);
			world.updateNeighborsAlways(pos.down(), state.getBlock());
			if ((state.get(Properties.STRAIGHT_RAIL_SHAPE)).isAscending()) {
				world.updateNeighborsAlways(pos.up(), state.getBlock());
			}
		}
	}
	
	private boolean isPowered(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		return WorldHelper.isPowered(world, pos, state, onScheduledTick, getQC(state), randQC(state)) || isPoweredByOtherRails(world, pos, state, true, 0) || isPoweredByOtherRails(world, pos, state, false, 0);
	}
	
	private DirectionalBooleanSetting getQC(BlockState state) {
		return state.isOf(Blocks.ACTIVATOR_RAIL) ? redstonetweaks.setting.Settings.ActivatorRail.QC : redstonetweaks.setting.Settings.PoweredRail.QC;
	}
	
	private boolean randQC(BlockState state) {
		return state.isOf(Blocks.ACTIVATOR_RAIL) ? redstonetweaks.setting.Settings.ActivatorRail.RANDOMIZE_QC.get() : redstonetweaks.setting.Settings.PoweredRail.RANDOMIZE_QC.get();
	}

	private int getDelay(BlockState state, boolean currentlyPowered) {
		if (state.isOf(Blocks.ACTIVATOR_RAIL)) {
			return currentlyPowered ? redstonetweaks.setting.Settings.ActivatorRail.DELAY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.ActivatorRail.DELAY_RISING_EDGE.get();
		} else {
			return currentlyPowered ? redstonetweaks.setting.Settings.PoweredRail.DELAY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.PoweredRail.DELAY_RISING_EDGE.get();
		}
	}

	private TickPriority getTickPriority(BlockState state, boolean currentlyPowered) {
		if (state.isOf(Blocks.ACTIVATOR_RAIL)) {
			return currentlyPowered ? redstonetweaks.setting.Settings.ActivatorRail.TICK_PRIORITY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.ActivatorRail.TICK_PRIORITY_RISING_EDGE.get();
		} else {
			return currentlyPowered ? redstonetweaks.setting.Settings.PoweredRail.TICK_PRIORITY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.PoweredRail.TICK_PRIORITY_RISING_EDGE.get();
		}
	}

	private boolean isLazy(BlockState state, boolean currentlyPowered) {
		if (state.isOf(Blocks.ACTIVATOR_RAIL)) {
			return currentlyPowered ? redstonetweaks.setting.Settings.ActivatorRail.LAZY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.ActivatorRail.LAZY_RISING_EDGE.get();
		} else {
			return currentlyPowered ? redstonetweaks.setting.Settings.PoweredRail.LAZY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.PoweredRail.LAZY_RISING_EDGE.get();
		}
	}
}

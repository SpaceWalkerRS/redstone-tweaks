package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIRedstoneDiode;
import redstonetweaks.interfaces.mixin.RTIServerTickScheduler;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends AbstractRedstoneGateBlock implements RTIRedstoneDiode {
	
	protected ComparatorBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	@Shadow protected abstract int calculateOutputSignal(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getPower(World world, BlockPos pos, BlockState state);
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@ModifyConstant(method = "getUpdateDelayInternal", constant = @Constant(intValue = 2))
	private int onGetUpdateDelayInternalModify2(int oldDelay) {
		return Tweaks.Comparator.DELAY.get();
	}
	
	@Redirect(method = "calculateOutputSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComparatorBlock;getMaxInputLevelSides(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)I"))
	private int onCalculateOutputSignalRedirectGetMaxInputLevelSides(ComparatorBlock gate, WorldView world, BlockPos pos, BlockState state) {
		int sidePower = getMaxInputLevelSides(world, pos, state);
		return Tweaks.Comparator.ADDITION_MODE.get() ? - sidePower : sidePower;
	}
	
	@Inject(method = "hasPower", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	private void onHasPowerInjectAtReturn0(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(state.get(Properties.COMPARATOR_MODE) == ComparatorMode.SUBTRACT && Tweaks.Comparator.ADDITION_MODE.get() && getMaxInputLevelSides(world, pos, state) > 0);
		cir.cancel();
	}
	
	@Inject(method = "hasPower", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	private void onHasPowerInjectAtReturn2(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir, int backPower, int sidePower) {
		if (!cir.getReturnValueZ()) {
			if (state.get(Properties.COMPARATOR_MODE) == ComparatorMode.SUBTRACT && Tweaks.Comparator.ADDITION_MODE.get()) {
				cir.setReturnValue(backPower + sidePower > 0);
				cir.cancel();
			}
		}
	}
	
	@ModifyConstant(method = "getPower", constant = @Constant(intValue = 15))
	private int onGetPowerModify15(int oldValue) {
		return redstonetweaks.setting.settings.Tweaks.Global.POWER_MAX.get();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;isTicking(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;)Z"))
	private <T> boolean onUpdatePoweredRedirectIsTicking(TickScheduler<T> scheduler, BlockPos pos, T block, World world, BlockPos blockPos, BlockState state) {
		if (Tweaks.Comparator.MICRO_TICK_MODE.get()) {
			return world.isClient() || ((RTIServerWorld)world).hasBlockEvent(pos);
		}
		
		return scheduler.isTicking(pos, block);
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority onUpdatePoweredRedirectPriorityHigh(World world, BlockPos pos, BlockState state) {
		if (Tweaks.BugFixes.MC54711.get() && ((RTIRedstoneDiode)this).isChainBugOccurring(world, pos, state)) {
			return Tweaks.Comparator.TICK_PRIORITY.get();
		} else {
			return Tweaks.Comparator.TICK_PRIORITY_FACING_DIODE.get();
		}
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;NORMAL:Lnet/minecraft/world/TickPriority;"))
	private TickPriority onUpdatePoweredRedirectPriorityNormal() {
		return Tweaks.Comparator.TICK_PRIORITY.get();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;ILnet/minecraft/world/TickPriority;)V"))
	private <T> void onUpdatePoweredRedirectSchedule(TickScheduler<T> scheduler, BlockPos pos, T block, int delay, TickPriority priority, World world, BlockPos blockPos, BlockState state) {
		if (Tweaks.Comparator.MICRO_TICK_MODE.get()) {
			if (!world.isClient()) {
				((ServerWorld)world).addSyncedBlockEvent(pos, state.getBlock(), delay, 0);
			}
		} else {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Comparator.DELAY.get(), priority);
		}
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At("HEAD"))
	private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (Tweaks.Global.SPONTANEOUS_EXPLOSIONS.get()) {
			boolean receivesPower = hasPower(world, pos, state);
			boolean powered = state.get(POWERED);
			
			if (powered == receivesPower) {
				WorldHelper.createSpontaneousExplosion(world, pos);
				
				ci.cancel();
			}
		}
	}
	
	// To fix the chain bug without altering other behavior, we identify if the chain bug is occurring
	@Override
	public boolean isChainBugOccurring(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(Properties.HORIZONTAL_FACING);
		BlockPos frontPos = pos.offset(facing.getOpposite());
		BlockState frontState = world.getBlockState(frontPos);
		Direction frontFacing = frontState.get(Properties.HORIZONTAL_FACING);

		if (facing != frontFacing) {
			return false;
		}
		if (!state.isOf(Blocks.COMPARATOR)) {
			return false;
		}
		if (state.get(Properties.POWERED) == frontState.get(Properties.POWERED)) {
			return false;
		}
		
		return ((RTIServerTickScheduler)world.getBlockTickScheduler()).hasScheduledTickAtTime(frontPos, frontState.getBlock(), getUpdateDelayInternal(state));
	}
}

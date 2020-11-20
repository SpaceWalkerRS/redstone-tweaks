package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstonetweaks.interfaces.RTIRedstoneDiode;
import redstonetweaks.interfaces.RTIServerTickScheduler;
import redstonetweaks.setting.Tweaks;

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
		return redstonetweaks.setting.Tweaks.Global.POWER_MAX.get();
	}
	
	@Inject(method = "updatePowered", cancellable = true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private void onUpdatePoweredInjectBeforePriorityHigh(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (getUpdateDelayInternal(state) == 0) {
			if (!world.isClient()) {
				scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
			}
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority onUpdatePoweredRedirectPriorityHigh(World world, BlockPos pos, BlockState state) {
		if (Tweaks.BugFixes.MC54711.get() && ((RTIRedstoneDiode)this).isInputBugOccurring(world, pos, state)) {
			return Tweaks.Comparator.TICK_PRIORITY.get();
		} else {
			return Tweaks.Comparator.TICK_PRIORITY_FACING_DIODE.get();
		}
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;NORMAL:Lnet/minecraft/world/TickPriority;"))
	private TickPriority onUpdatePoweredRedirectPriorityNormal() {
		return Tweaks.Comparator.TICK_PRIORITY.get();
	}
	
	@ModifyConstant(method = "updatePowered", constant = @Constant(intValue = 2))
	private int onUpdatePowerModifyDelay(int oldDelay) {
		return Tweaks.Comparator.DELAY.get();
	}
	
	// To fix the chain bug without altering other behavior,
	// we identify if the chain bug is occuring
	@Override
	public boolean isInputBugOccurring(World world, BlockPos pos, BlockState state) {
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

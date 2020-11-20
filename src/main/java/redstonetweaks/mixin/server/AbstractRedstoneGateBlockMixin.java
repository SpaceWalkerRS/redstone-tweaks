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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstonetweaks.helper.RedstoneWireHelper;
import redstonetweaks.interfaces.RTIBlock;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.interfaces.RTIServerWorld;
import redstonetweaks.interfaces.RTIRedstoneDiode;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.UnfinishedEvent.Source;
import redstonetweaks.world.common.UpdateOrder;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin implements RTIBlock {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract boolean hasPower(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;hasPower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private void onScheduledTickInjectBeforeHasPower(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		boolean powered = state.get(Properties.POWERED);
		boolean lazy = powered ? Tweaks.Repeater.LAZY_FALLING_EDGE.get() : Tweaks.Repeater.LAZY_RISING_EDGE.get();
		boolean isReceivingPower = hasPower(world, pos, state);
		boolean shouldBePowered = lazy ? !powered : isReceivingPower;
		
		if (powered != shouldBePowered) {
			BlockState newState = state.with(Properties.POWERED, shouldBePowered);
			world.setBlockState(pos, newState, 2);
			
			if (shouldBePowered != isReceivingPower) {
				if (((RTIWorld)world).updateNeighborsNormally()) {
					scheduleTickOnScheduledTick(world, pos, newState, random);
				} else {
					((RTIServerWorld)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, newState, pos, 0);
				}
			}
		}
		
		ci.cancel();
	}
	
	@Redirect(method = "getStrongRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getWeakRedstonePower(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I"))
	private int onGetStrongRedstonePowerRedirectGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (state.isOf(Blocks.COMPARATOR)) {
			return state.getWeakRedstonePower(world, pos, direction);
		}
		return state.get(Properties.POWERED) && state.get(Properties.HORIZONTAL_FACING) == direction ? Tweaks.Repeater.POWER_STRONG.get() : 0;
	}
	
	@Inject(method = "updatePowered", cancellable =  true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private void updatePoweredInjectBeforePriorityHigh(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (getUpdateDelayInternal(state) == 0) {
			if (!world.isClient()) {
				scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
			}
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityHigh() {
		return Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;EXTREMELY_HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityExtremelyHigh(World world, BlockPos pos, BlockState state) {
		if (Tweaks.BugFixes.MC54711.get() && ((RTIRedstoneDiode)this).isInputBugOccurring(world, pos, state)) {
			return Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.get();
		}
		return Tweaks.Repeater.TICK_PRIORITY_FACING_DIODE.get();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;VERY_HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityVeryHigh() {
		return Tweaks.Repeater.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	@Inject(method = "getPower", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Ljava/lang/Math;max(II)I"))
	private void onGetPowerInjectBeforeMax(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir, Direction facing, BlockPos behindPos, int power, BlockState behindState) {
		if (behindState.isOf(Blocks.REDSTONE_WIRE) && RedstoneWireHelper.emitsPowerTo(world, behindPos, facing)) {
			power = Math.max(power, behindState.getWeakRedstonePower(world, behindPos, facing));
		}
		
		cir.setReturnValue(power);
		cir.cancel();
	}
	
	@SuppressWarnings("unchecked")
	@Redirect(method = "getInputLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
	private <T extends Comparable<T>> T onGetInputLevelRedirectGetProperty(BlockState state, Property<T> property, WorldView world, BlockPos pos, Direction dir) {
		return (T)(Integer)state.getWeakRedstonePower(world, pos, dir);
	}
	
	@ModifyConstant(method = "getInputLevel", constant = @Constant(intValue = 15))
	private int onGetInputLevelModifyRedstoneBlockPower(int oldPower) {
		return Tweaks.Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT.get() ? Tweaks.RedstoneBlock.POWER_WEAK.get() : 0;
	}
	
	@Inject(method = "updateTarget", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateTargetInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		UpdateOrder updateOrder = state.isOf(Blocks.COMPARATOR) ? Tweaks.Comparator.BLOCK_UPDATE_ORDER.get() : Tweaks.Repeater.BLOCK_UPDATE_ORDER.get();
		updateOrder.dispatchBlockUpdates(world, pos, state.getBlock(), state.get(Properties.HORIZONTAL_FACING).getOpposite());
		
		ci.cancel();
	}
	
	@ModifyConstant(method = "getOutputLevel", constant = @Constant(intValue = 15))
	private int getWeakRedstonePower(int oldValue) {
		return Tweaks.Repeater.POWER_WEAK.get();
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		if (type == 0) {
			scheduleTickOnScheduledTick((ServerWorld)world, pos, state, world.getRandom());
		}
		
		return false;
	}
	
	private void scheduleTickOnScheduledTick(ServerWorld world, BlockPos pos, BlockState state, Random random) {
		boolean powered = state.get(Properties.POWERED);
		int delay = powered ? Tweaks.Repeater.DELAY_FALLING_EDGE.get() : Tweaks.Repeater.DELAY_RISING_EDGE.get();
		
		if (delay == 0) {
			scheduledTick(state, world, pos, random);
		} else { 
			TickPriority priority = powered ? Tweaks.Repeater.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.get();
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), getUpdateDelayInternal(state), priority);
		}
	}
}

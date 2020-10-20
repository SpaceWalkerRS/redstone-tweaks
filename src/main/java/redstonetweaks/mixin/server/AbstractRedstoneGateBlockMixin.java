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

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.RedstoneDiodeHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.Settings;
import redstonetweaks.world.common.UpdateOrder;
import redstonetweaks.world.common.UnfinishedEvent.Source;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin implements BlockHelper {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract boolean hasPower(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;hasPower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private void onScheduledTickInjectBeforeHasPower(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		boolean powered = state.get(Properties.POWERED);
		boolean lazy = powered ? Settings.Repeater.LAZY_FALLING_EDGE.get() : Settings.Repeater.LAZY_RISING_EDGE.get();
		boolean isReceivingPower = hasPower(world, pos, state);
		boolean shouldBePowered = lazy ? !powered : isReceivingPower;
		
		if (powered != shouldBePowered) {
			world.setBlockState(pos, state.with(Properties.POWERED, shouldBePowered), 2);
			
			if (shouldBePowered != isReceivingPower) {
				if (((WorldHelper)world).updateNeighborsNormally()) {
					updatePoweredOnScheduledTick(world, pos, state, random, !powered);
				} else {
					((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, state, pos, 0);
				}
			}
		}
	}
	
	@Redirect(method = "getStrongRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getWeakRedstonePower(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I"))
	private int onGetStrongRedstonePowerRedirectGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (state.isOf(Blocks.COMPARATOR)) {
			return state.getWeakRedstonePower(world, pos, direction);
		}
		return state.get(Properties.POWERED) && state.get(Properties.HORIZONTAL_FACING) == direction ? Settings.Repeater.POWER_STRONG.get() : 0;
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
		return Settings.Repeater.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;EXTREMELY_HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityExtremelyHigh(World world, BlockPos pos, BlockState state) {
		if (Settings.BugFixes.MC54711.get() && ((RedstoneDiodeHelper)this).isInputBugOccurring(world, pos, state)) {
			return Settings.Repeater.TICK_PRIORITY_RISING_EDGE.get();
		}
		return Settings.Repeater.TICK_PRIORITY_FACING_DIODE.get();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;VERY_HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityVeryHigh() {
		return Settings.Repeater.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	@Inject(method = "getPower", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private void onGetPowerInjectBeforeGetBlockState(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir, Direction facing, BlockPos behindPos, int power) {
		BlockState behindState = WorldHelper.getStateForPower(world, behindPos, facing.getOpposite());
		
		cir.setReturnValue(Math.max(power, behindState.isOf(Blocks.REDSTONE_WIRE) ? behindState.getWeakRedstonePower(world, behindPos, facing) : 0));
		cir.cancel();
	}
	
	@SuppressWarnings("unchecked")
	@Redirect(method = "getInputLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
	private <T extends Comparable<T>> T onGetInputLevelRedirectGetProperty(BlockState state, Property<T> property, WorldView world, BlockPos pos, Direction dir) {
		return (T)(Integer)state.getWeakRedstonePower(world, pos, dir);
	}
	
	@ModifyConstant(method = "getInputLevel", constant = @Constant(intValue = 15))
	private int onGetInputLevelModifyRedstoneBlockPower(int oldPower) {
		return Settings.Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT.get() ? Settings.RedstoneBlock.POWER_WEAK.get() : 0;
	}
	
	@Inject(method = "updateTarget", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateTargetInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		UpdateOrder updateOrder = state.isOf(Blocks.COMPARATOR) ? Settings.Comparator.BLOCK_UPDATE_ORDER.get() : Settings.Repeater.BLOCK_UPDATE_ORDER.get();
		updateOrder.dispatchBlockUpdates(world, pos, state.getBlock(), state.get(Properties.HORIZONTAL_FACING).getOpposite());
		
		ci.cancel();
	}
	
	@ModifyConstant(method = "getOutputLevel", constant = @Constant(intValue = 15))
	private int getWeakRedstonePower(int oldValue) {
		return Settings.Repeater.POWER_WEAK.get();
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		if (type == 0) {
			updatePoweredOnScheduledTick((ServerWorld)world, pos, state, world.getRandom(), state.get(Properties.POWERED));
		}
		
		return false;
	}
	
	private void updatePoweredOnScheduledTick(ServerWorld world, BlockPos pos, BlockState state, Random random, boolean powered) {
		int delay = powered ? Settings.Repeater.DELAY_FALLING_EDGE.get() : Settings.Repeater.DELAY_RISING_EDGE.get();
		
		if (delay == 0) {
			scheduledTick(world.getBlockState(pos), world, pos, random);
		} else { 
			TickPriority priority = powered ? Settings.Repeater.TICK_PRIORITY_FALLING_EDGE.get() : Settings.Repeater.TICK_PRIORITY_RISING_EDGE.get();
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), state.get(Properties.DELAY) * delay, priority);
		}
	}
}

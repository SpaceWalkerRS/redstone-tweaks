package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.block.entity.PowerBlockEntity;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIBlock;
import redstonetweaks.interfaces.mixin.RTIPressurePlate;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class AbstractPressurePlateBlockMixin extends Block implements RTIBlock {
	
	public AbstractPressurePlateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow protected abstract void updatePlateState(World world, BlockPos pos, BlockState blockState, int rsOut);
	@Shadow protected abstract int getRedstoneOutput(BlockState state);
	
	// When the pressure plate is ticked, it should call updatePlateState
	// regardless of its current redstone output, in case the plate
	// has activation delay and the plate is ticked to power on.
	@ModifyConstant(method = "scheduledTick", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO))
	private int onScheduledTickModifyCompareValue(int oldValue) {
		return -1;
	}
	
	@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;updatePlateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V"))
	public void onEntityCollisionRedirectUpdatePlateState(AbstractPressurePlateBlock pressurePlate, World world, BlockPos pos, BlockState state, int i) {
		int delay = ((RTIPressurePlate)this).delayRisingEdge(state);
		TickPriority priority = ((RTIPressurePlate)this).tickPriorityRisingEdge(state);
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
	}
	
	@Inject(method = "updatePlateState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onUpdatePlateStateInjectBeforeSetBlockState(World world, BlockPos pos, BlockState state, int rsOut, CallbackInfo ci, int newPower) {
		if (hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PowerBlockEntity) {
				((PowerBlockEntity)blockEntity).setPower(newPower);
			}
		}
	}
	
	@ModifyArg(method = "updatePlateState", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private int onUpdatePlateStateModifySetBlockStateFlags(int oldFlags) {
		return oldFlags & 16;
	}
	
	@Inject(method = "updatePlateState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onUpdatePlateStateInjectAfterSetBlockState(World world, BlockPos pos, BlockState state, int rsOut, CallbackInfo ci, int newPower, boolean bl, boolean bl2, BlockState newState) {
		newState.updateNeighbors(world, pos, 2);
	}
	
	@Redirect(method = "updatePlateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void updatePlateStateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay, World world, BlockPos blockPos, BlockState state) {
		if (((RTIWorld)world).immediateNeighborUpdates()) {
			int delay = ((RTIPressurePlate)this).delayFallingEdge(state);
			TickPriority priority = ((RTIPressurePlate)this).tickPriorityFallingEdge(state);
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
		} else if (!world.isClient()) {
			((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 0, state.getBlock());
		}
	}
	
	@Inject(method = "updateNeighbors", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, CallbackInfo ci) {
		BlockState state = world.getBlockState(pos);
		((RTIWorld)world).dispatchBlockUpdates(pos, null, state.getBlock(), ((RTIPressurePlate)this).updateOrder(state));
		
		ci.cancel();
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(((RTIPressurePlate)this).powerWeak(world, pos, state));
		cir.cancel();
	}
	
	@Inject(method = "getStrongRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetStrongRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		int power = direction == Direction.UP ? ((RTIPressurePlate)this).powerStrong(world, pos, state) : 0;
		
		cir.setReturnValue(power);
		cir.cancel();
	}
	
	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		BlockState state = world.getBlockState(pos);
		
		if (state.isOf((Block)(Object)this)) {
			int delay = ((RTIPressurePlate)this).delayFallingEdge(state);
			TickPriority priority = ((RTIPressurePlate)this).tickPriorityFallingEdge(state);
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
		}
		
		return false;
	}
}

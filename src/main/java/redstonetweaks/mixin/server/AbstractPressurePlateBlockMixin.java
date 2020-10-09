package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import redstonetweaks.helper.PressurePlateHelper;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class AbstractPressurePlateBlockMixin {
	
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
		int delay = ((PressurePlateHelper)this).delayRisingEdge(state);
		if (delay == 0) {
			updatePlateState(world, pos, state, i);
		} else {
			TickPriority priority = ((PressurePlateHelper)this).tickPriorityRisingEdge(state);
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, priority);
		}
	}
	
	@Redirect(method = "updatePlateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void updatePlateStateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay, World world, BlockPos blockPos, BlockState state) {
		int delay = ((PressurePlateHelper)this).delayFallingEdge(state);
		TickPriority priority = ((PressurePlateHelper)this).tickPriorityFallingEdge(state);
		
		tickScheduler.schedule(pos, block, delay, priority);
	}
	
	@Inject(method = "updateNeighbors", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, CallbackInfo ci) {
		BlockState state = world.getBlockState(pos);
		((PressurePlateHelper)this).updateOrder(state).dispatchBlockUpdates(world, pos, state.getBlock());
		
		ci.cancel();
	}
	
	@Redirect(method = "getWeakRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;getRedstoneOutput(Lnet/minecraft/block/BlockState;)I"))
	private int onGetWeakRedstonePowerRedirectGetRedstoneOutput(AbstractPressurePlateBlock plate, BlockState state) {
		return ((PressurePlateHelper)this).powerWeak(state);
	}
	
	@Redirect(method = "getStrongRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;getRedstoneOutput(Lnet/minecraft/block/BlockState;)I"))
	private int onGetStrongRedstonePowerRedirectGetRedstoneOutput(AbstractPressurePlateBlock plate, BlockState state) {
		return ((PressurePlateHelper)this).powerStrong(state);
	}
}

package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PressurePlateOverrides;

@Mixin(BasePressurePlateBlock.class)
public abstract class BasePressurePlateBlockMixin implements PressurePlateOverrides {

	@Inject(
		method = "getPressedTime",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelayFallingEdge(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(delayFallingEdge());
	}

	// When the pressure plate is ticked, it should call updatePlateState
	// regardless of its current redstone output, in case the plate
	// has rising edge delay and the plate is ticked to power on.
	@ModifyConstant(
		method = "tick",
		constant = @Constant(
			expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO
		)
	)
	private int rtTweakRisingEdgeDelay(int zero) {
		return -1;
	}

	@Redirect(
		method = "entityInside",
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;checkPressed(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)V"
		)
	)
	public void rtTweakRisingEdgeDelayAndTickPriority(BasePressurePlateBlock pressurePlate, Entity entity, Level level, BlockPos pos, BlockState state, int signal) {
		int delay = delayRisingEdge();
		TickPriority tickPriority = tickPriorityRisingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, tickPriority);
	}

	@Redirect(
		method = "checkPressed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakFallingEdgeDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, Entity entity, Level level, BlockPos pos, BlockState state, int signal) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, tickPriorityFallingEdge());
	}
}

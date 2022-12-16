package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PressurePlateOverrides;

@Mixin(WeightedPressurePlateBlock.class)
public class WeightedPressurePlateBlockMixin implements PressurePlateOverrides {

	@Shadow private int maxWeight;

	@Inject(
		method = "getSignalStrength",
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakWeight(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (block() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			maxWeight = Tweaks.HeavyWeightedPressurePlate.weight();
		}
		if (block() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			maxWeight = Tweaks.LightWeightedPressurePlate.weight();
		}
	}
	
	@Inject(
		method = "getPressedTime",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelayFallingEdge(CallbackInfoReturnable<Integer> cir) {
		if (block() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			cir.setReturnValue(Tweaks.HeavyWeightedPressurePlate.delayFallingEdge());
		}
		if (block() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			cir.setReturnValue(Tweaks.LightWeightedPressurePlate.delayFallingEdge());
		}
	}

	@Override
	public int delayRisingEdge() {
		if (block() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.HeavyWeightedPressurePlate.delayRisingEdge();
		}
		if (block() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.LightWeightedPressurePlate.delayRisingEdge();
		}

		return 0;
	}

	@Override
	public int delayFallingEdge() {
		if (block() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.HeavyWeightedPressurePlate.delayFallingEdge();
		}
		if (block() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.LightWeightedPressurePlate.delayFallingEdge();
		}

		return 10;
	}

	@Override
	public TickPriority tickPriorityRisingEdge() {
		if (block() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.HeavyWeightedPressurePlate.tickPriorityRisingEdge();
		}
		if (block() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.LightWeightedPressurePlate.tickPriorityRisingEdge();
		}

		return TickPriority.NORMAL;
	}

	@Override
	public TickPriority tickPriorityFallingEdge() {
		if (block() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.HeavyWeightedPressurePlate.tickPriorityFallingEdge();
		}
		if (block() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			return Tweaks.LightWeightedPressurePlate.tickPriorityFallingEdge();
		}

		return TickPriority.NORMAL;
	}
}

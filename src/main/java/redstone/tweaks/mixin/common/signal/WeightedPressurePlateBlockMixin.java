package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PressurePlateOverrides;

@Mixin(WeightedPressurePlateBlock.class)
public abstract class WeightedPressurePlateBlockMixin implements PressurePlateOverrides {

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

}

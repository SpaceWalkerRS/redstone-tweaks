package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.PoweredRailBlock;

import redstone.tweaks.interfaces.mixin.PoweredRailOverrides;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements PoweredRailOverrides {

	@ModifyConstant(
		method = "findPoweredRailSignal",
		constant = @Constant(
			intValue = 8
		)
	)
	private int rtTweakPowerLimit(int powerLimit) {
		return powerLimit() - 1;
	}
}

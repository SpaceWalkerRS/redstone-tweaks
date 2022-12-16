package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.LeverBlock;

import redstone.tweaks.Tweaks;

@Mixin(LeverBlock.class)
public class LeverBlockMixin {

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = 15
		)
	)
	private int rtTweakSignal(int signal) {
		return Tweaks.Lever.signal();
	}

	@ModifyConstant(
		method = "getDirectSignal",
		constant = @Constant(
			intValue = 15
		)
	)
	private int rtTweakDirectSignal(int signal) {
		return Tweaks.Lever.signalDirect();
	}
}

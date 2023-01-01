package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin {

	@Shadow boolean arrowsCanPress;

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakSignal(int signal) {
		return Tweaks.Button.signal(arrowsCanPress);
	}

	@ModifyConstant(
		method = "getDirectSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakDirectSignal(int signal) {
		return Tweaks.Button.signalDirect(arrowsCanPress);
	}
}

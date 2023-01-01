package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakSignal(int signal) {
		return Tweaks.DetectorRail.signal();
	}

	@ModifyConstant(
		method = "getDirectSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakDirectSignal(int signal) {
		return Tweaks.DetectorRail.signalDirect();
	}
}

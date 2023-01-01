package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.RedstoneTorchOverrides;

@Mixin(RedstoneWallTorchBlock.class)
public abstract class RedstoneWallTorchBlockMixin implements RedstoneTorchOverrides {

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakSignal(int signal) {
		return requestDirectSignal() ? Tweaks.RedstoneTorch.signalDirect() : Tweaks.RedstoneTorch.signal();
	}
}

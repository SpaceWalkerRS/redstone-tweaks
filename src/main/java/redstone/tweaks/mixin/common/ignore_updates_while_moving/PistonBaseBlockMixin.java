package redstone.tweaks.mixin.common.ignore_updates_while_moving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Inject(
		method = "checkIfExtend",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtIgnoreUpdateWhileExtending(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (Tweaks.Piston.ignoreUpdatesWhileExtending(isSticky()) && state.getValue(PistonBaseBlock.EXTENDED)) {
			Direction facing = state.getValue(PistonBaseBlock.FACING);
			BlockPos frontPos = pos.relative(facing);

			if (PistonOverrides.isExtendingHead(level, frontPos, facing, isSticky())) {
				ci.cancel();
			}
		}
	}
}

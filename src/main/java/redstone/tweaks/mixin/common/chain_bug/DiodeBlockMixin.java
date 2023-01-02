package redstone.tweaks.mixin.common.chain_bug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(DiodeBlock.class)
public abstract class DiodeBlockMixin implements DiodeOverrides {

	@Inject(
		method = "shouldPrioritize",
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/DiodeBlock;isDiode(Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private void rtFixMC54711(BlockGetter blockGetter, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir, Direction facing, BlockState frontState) {
		if (Tweaks.BugFixes.MC54711() && blockGetter instanceof Level) {
			Level level = (Level)blockGetter;

			if (frontState.is(block()) && level.getBlockTicks().hasScheduledTick(pos.relative(facing), frontState.getBlock())) {
				cir.setReturnValue(false);
			}
		}
	}
}

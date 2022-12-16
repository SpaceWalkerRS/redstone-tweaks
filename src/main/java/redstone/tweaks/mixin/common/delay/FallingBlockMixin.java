package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin {

	@Redirect(
		method = "onPlace",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.FallingBlock.tickPriority());
	}

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.FallingBlock.tickPriority());
	}

	@Inject(
		method = "getDelayAfterPlace",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelay(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(Tweaks.FallingBlock.delay());
	}
}

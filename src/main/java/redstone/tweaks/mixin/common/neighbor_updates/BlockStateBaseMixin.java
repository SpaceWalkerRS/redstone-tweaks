package redstone.tweaks.mixin.common.neighbor_updates;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;

@Mixin(BlockStateBase.class)
public class BlockStateBaseMixin {

	@Shadow private BlockState asState() { return null; }

	@Inject(
		method = "neighborChanged",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDoBlockUpdates(Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		if (!Tweaks.Global.doBlockUpdates()) {
			ci.cancel();
		}
	}

	@Inject(
		method = "updateShape",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDoShapeUpdates(Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (!Tweaks.Global.doShapeUpdates()) {
			cir.setReturnValue(asState());
		}
	}
}

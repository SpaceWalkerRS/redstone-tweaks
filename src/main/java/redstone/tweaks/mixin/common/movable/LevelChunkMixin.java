package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import redstone.tweaks.interfaces.mixin.ILevel;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

	@Shadow @Final private Level level;

	@Inject(
		method = "setBlockState",
		at = @At(
			value = "FIELD",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/Level;isClientSide:Z"
		)
	)
	private void rtPlaceMovedBlockEntity(BlockPos pos, BlockState state, boolean movedByPiston, CallbackInfoReturnable<BlockState> cir) {
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = ((ILevel)level).getMovedBlockEntityForPlacement(pos, state);

			if (blockEntity != null) {
				((LevelChunk)(Object)this).addAndRegisterBlockEntity(blockEntity);
			}
		}
	}
}

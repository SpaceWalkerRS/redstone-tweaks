package redstone.tweaks.mixin.client.move_self;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonHeadRenderer.class)
public class PistonHeadRendererMixin {

	@Shadow private void renderBlock(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, boolean cull, int _overlay) { }

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			ordinal = 3,
			target = "Lnet/minecraft/client/renderer/blockentity/PistonHeadRenderer;renderBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;ZI)V"
		)
	)
	private void rtRenderMoveSelf(PistonHeadRenderer renderer, BlockPos pos, BlockState state, PoseStack _stack, MultiBufferSource _bufferSource, Level level, boolean cull, int _overlay, PistonMovingBlockEntity mbe, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
		boolean isExtendingSourceBase = mbe.isSourcePiston() && mbe.isExtending() && PistonOverrides.isBase(level, pos, state);

		if (isExtendingSourceBase) {
			state = state.setValue(PistonBaseBlock.EXTENDED, true);
		}

		renderBlock(pos, state, stack, bufferSource, level, cull, overlay);

		stack.popPose();
		stack.pushPose();

		if (isExtendingSourceBase) {
			Direction facing = state.getValue(PistonBaseBlock.FACING);

			if (mbe.getDirection() == facing) {
				boolean isSticky = PistonOverrides.isBaseSticky(state);
				boolean isShort = mbe.getProgress(0.0F) >= 0.5F;

				BlockPos headPos = pos.relative(facing);
				BlockState headState = Blocks.PISTON_HEAD.defaultBlockState().
					setValue(PistonHeadBlock.FACING, facing).
					setValue(PistonHeadBlock.TYPE, isSticky ? PistonType.STICKY : PistonType.DEFAULT).
					setValue(PistonHeadBlock.SHORT, isShort);

				renderBlock(headPos, headState, stack, bufferSource, level, true, overlay);
			}
		}
	}
}

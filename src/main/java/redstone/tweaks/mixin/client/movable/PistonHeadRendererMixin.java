package redstone.tweaks.mixin.client.movable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;

import redstone.tweaks.interfaces.mixin.IPistonMovingBlockEntity;

@Mixin(PistonHeadRenderer.class)
public class PistonHeadRendererMixin {

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/renderer/blockentity/PistonHeadRenderer;renderBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;ZI)V"
		)
	)
	private void rtRenderMovedBlockEntity(PistonMovingBlockEntity movingBlockEntity, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, CallbackInfo ci) {
		BlockEntity blockEntity = ((IPistonMovingBlockEntity)movingBlockEntity).getMovedBlockEntity();

		if (blockEntity != null) {
			Minecraft.getInstance().getBlockEntityRenderDispatcher().render(blockEntity, partialTick, stack, bufferSource);
		}
	}
}

package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import redstonetweaks.interfaces.RTIPistonBlockEntity;

@Mixin(PistonBlockEntityRenderer.class)
public class PistonBlockEntityRendererMixin {
	
	@Inject(method = "render", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/render/block/entity/PistonBlockEntityRenderer;method_3575(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;ZI)V"))
	private void onRenderInjectAfterMethod_3575(PistonBlockEntity pistonBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfo ci) {
		BlockEntity pushedBlockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getPushedBlockEntity();
		
		if (pushedBlockEntity != null) {
			BlockEntityRenderer<BlockEntity> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(pushedBlockEntity);
			
			if (blockEntityRenderer != null) {
				blockEntityRenderer.render(pushedBlockEntity, tickDelta, matrixStack, vertexConsumerProvider, light, overlay);
			}
		}
	}
}

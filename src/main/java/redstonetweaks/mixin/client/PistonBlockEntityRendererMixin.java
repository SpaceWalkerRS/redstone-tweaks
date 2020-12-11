package redstonetweaks.mixin.client;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonBlockEntity;

@Mixin(PistonBlockEntityRenderer.class)
public class PistonBlockEntityRendererMixin {
	
	// Moved piston heads should always render with the long arm
	@ModifyConstant(method = "render", constant = @Constant(floatValue = 0.5f))
	private float onRenderModifyFloat1(float oldValue, PistonBlockEntity pistonBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		return pistonBlockEntity.isSource() ? oldValue : -1.0f;
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/render/block/entity/PistonBlockEntityRenderer;method_3575(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;ZI)V"))
	private void onRenderInjectAfterMethod_3575(PistonBlockEntity pistonBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfo ci) {
		BlockEntity movedBlockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getPushedBlockEntity();
		
		if (movedBlockEntity != null) {
			BlockEntityRenderer<BlockEntity> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(movedBlockEntity);
			
			if (blockEntityRenderer != null) {
				blockEntityRenderer.render(movedBlockEntity, tickDelta, matrixStack, vertexConsumerProvider, light, overlay);
			}
		}
		
		BlockState pushedBlock = pistonBlockEntity.getPushedBlock();
		if (((RTIPistonBlockEntity)pistonBlockEntity).isMergingSlabs() && SlabHelper.isSlab(pushedBlock)) {
			// Undo the offset
			matrixStack.pop();
			
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			
			World world = pistonBlockEntity.getWorld();
			BlockPos pos = pistonBlockEntity.getPos();
			BlockState state = pushedBlock.with(Properties.SLAB_TYPE, SlabHelper.getOppositeType(pushedBlock.get(Properties.SLAB_TYPE)));
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayers.getBlockLayer(state));
			Random random = world.getRandom();
			
			blockRenderManager.renderBlock(state, pos, world, matrixStack, vertexConsumer, true, random);
			
			// Push a new entry onto the stack
			matrixStack.push();
		}
	}
}

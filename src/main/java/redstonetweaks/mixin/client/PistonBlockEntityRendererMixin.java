package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIWorld;

@Mixin(PistonBlockEntityRenderer.class)
public abstract class PistonBlockEntityRendererMixin {
	
	@Shadow protected abstract void method_3575(BlockPos pos, BlockState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, World world, boolean cull, int overlay);
	
	@ModifyVariable(method = "render", argsOnly = true, ordinal = 0, at = @At(value = "HEAD"))
	private float onRenderModifyTickDelta(float oldTickDelta, PistonBlockEntity pistonBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		World world = pistonBlockEntity.getWorld();
		
		if (world != null && !((RTIWorld)world).normalWorldTicks()) {
			return 0.2F;
		}
		
		return oldTickDelta;
	}
	
	@Inject(method = "render", cancellable = true, at = @At(value = "HEAD"))
	private void onRenderInjectAtHead(PistonBlockEntity pistonBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfo ci) {
		World world = pistonBlockEntity.getWorld();
		BlockState movedState = pistonBlockEntity.getPushedBlock();
		BlockEntity movedBlockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedBlockEntity();
		
		if (world == null || movedState.isAir()) {
			return;
		}
		
		BlockModelRenderer.enableBrightnessCache();
		
		float offsetX = pistonBlockEntity.getRenderOffsetX(tickDelta);
		float offsetY = pistonBlockEntity.getRenderOffsetY(tickDelta);
		float offsetZ = pistonBlockEntity.getRenderOffsetZ(tickDelta);
		
		float totalOffset = Math.abs(offsetX + offsetY + offsetZ);
		
		matrixStack.push();
		matrixStack.translate(offsetX, offsetY, offsetZ);
		
		Direction dir = pistonBlockEntity.getMovementDirection().getOpposite();
		BlockPos toPos = pistonBlockEntity.getPos();
		BlockPos fromPos = toPos.offset(dir);
		
		if (pistonBlockEntity.isSource()) {
			boolean isExtending = pistonBlockEntity.isExtending();
			boolean sourceIsMoving = ((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving();
			
			boolean renderHead = true;
			
			if (sourceIsMoving) {
				// Render a piston base that is pushing itself backwards or pulling itself forward
				method_3575(fromPos, movedState.with(Properties.EXTENDED, true), matrixStack, vertexConsumerProvider, world, false, overlay);
				
				// Undo the offset so the piston head is rendered stationary
				matrixStack.pop();
				matrixStack.push();
				
				if (isExtending) {
					if (PistonHelper.isPistonHead(world.getBlockState(fromPos), PistonHelper.isSticky(movedState), dir)) {
						// The head should be rendered in front of the base
						matrixStack.translate(dir.getOffsetX(), dir.getOffsetY(), dir.getOffsetZ());
					} else {
						// Don't render a head if there is no piston head in the world at that location
						renderHead = false;
					}
				}
			}
			
			if (renderHead) {
				PistonType pistonType = PistonHelper.isPistonHead(movedState) ? movedState.get(Properties.PISTON_TYPE) : (PistonHelper.isSticky(movedState) ? PistonType.STICKY : PistonType.DEFAULT);
				Direction facing = movedState.get(Properties.FACING);
				boolean shortArm = isExtending ? totalOffset > 0.5F : totalOffset < 0.5F;
				
				BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(Properties.PISTON_TYPE, pistonType).with(Properties.FACING, facing).with(Properties.SHORT, shortArm);
				
				method_3575(fromPos, pistonHead, matrixStack, vertexConsumerProvider, world, false, overlay);
			}
			
			if (!isExtending && !sourceIsMoving) {
				// Undo the offset so the base is rendered stationary
				matrixStack.pop();
				matrixStack.push();
				
				method_3575(toPos, movedState.with(Properties.EXTENDED, true), matrixStack, vertexConsumerProvider, world, false, overlay);
			}
		} else {
			if (PistonHelper.isPistonHead(movedState) && ((RTIPistonBlockEntity)pistonBlockEntity).isMerging()) {
				if (totalOffset > 0.5F) {
					movedState = movedState.with(Properties.SHORT, true);
				}
			}
			
			method_3575(fromPos, movedState, matrixStack, vertexConsumerProvider, world, false, overlay);
			
			if (movedBlockEntity != null) {
				BlockEntityRenderDispatcher.INSTANCE.render(movedBlockEntity, tickDelta, matrixStack, vertexConsumerProvider);
			}
			
			matrixStack.pop();
			matrixStack.push();
			
			BlockState mergingState = ((RTIPistonBlockEntity)pistonBlockEntity).getMergingState();
			BlockEntity mergingBlockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMergingBlockEntity();
			
			if (mergingState != null) {
				if (PistonHelper.isPistonHead(mergingState) && totalOffset > 0.5F) {
					mergingState = mergingState.with(Properties.SHORT, true);
				}
				
				method_3575(toPos, mergingState, matrixStack, vertexConsumerProvider, world, false, overlay);
			}
			if (mergingBlockEntity != null) {
				BlockEntityRenderDispatcher.INSTANCE.render(mergingBlockEntity, tickDelta, matrixStack, vertexConsumerProvider);
			}
		}
		
		matrixStack.pop();
		
		BlockModelRenderer.disableBrightnessCache();
		
		ci.cancel();
	}
}

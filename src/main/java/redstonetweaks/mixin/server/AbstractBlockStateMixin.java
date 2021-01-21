package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.mixinterfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
	
	@Inject(method = "isSolidBlock", cancellable = true, at = @At(value = "HEAD"))
	private void onIsSolidBlockInjectAtHead(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (((AbstractBlockState)(Object)this).isOf(Blocks.WHITE_CONCRETE_POWDER)) {
			cir.setReturnValue(Tweaks.WhiteConcretePowder.IS_SOLID.get());
			cir.cancel();
		}
	}
	
	@Inject(method = "isSideSolid", cancellable = true, at = @At(value = "HEAD"))
	private void onIsSideSolidInjectAtReturn(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType, CallbackInfoReturnable<Boolean> cir) {
		if (BlockHelper.isSideSolid(world, pos, direction, (BlockState)(Object)this, shapeType)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}

	@Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
		((RTIWorld)world).dispatchShapeUpdatesAround(pos, pos, world.getBlockState(pos), flags, maxUpdateDepth);
		ci.cancel();
	}
}

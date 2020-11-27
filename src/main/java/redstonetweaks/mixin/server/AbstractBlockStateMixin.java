package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.interfaces.RTIWorld;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

	@Inject(method = "isSideSolidFullSquare", cancellable = true, at = @At(value = "RETURN"))
	private void onIsSideSolidFullSquareInjectAtReturn(BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			boolean isSolid = false;
			
			if (BlockHelper.isRigidPistonBase(world, pos, (BlockState)(Object)this)) {
				isSolid = true;
			} else
			if (BlockHelper.isStationarySlab(world, pos, (BlockState)(Object)this, direction)) {
				isSolid = true;
			}
			
			if (isSolid) {
				cir.setReturnValue(true);
				cir.cancel();
			}
		}
	}

	@Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
		((RTIWorld)world).dispatchShapeUpdatesAround(pos, pos, world.getBlockState(pos), flags, maxUpdateDepth);
		ci.cancel();
	}
}

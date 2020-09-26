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
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.settings.Settings;
import redstonetweaks.world.server.ScheduledNeighborUpdate.UpdateType;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

	@Inject(method = "isSideSolidFullSquare", cancellable = true, at = @At(value = "RETURN"))
	private void onIsSideSolidFullSquareInjectAtReturn(BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ()) {
			cir.setReturnValue(BlockHelper.isRigidPistonBase(world, pos, (BlockState)(Object)this));
			cir.cancel();
		}
	}

	@Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
		if (Settings.Global.DO_SHAPE_UPDATES.get()) {
			if (!((WorldHelper) world).updateNeighborsNormally()) {
				if (!world.isClient()) {
					for (Direction direction : BlockHelper.FACINGS) {
						BlockPos neighborPos = pos.offset(direction);
						((ServerWorldHelper)world).getNeighborUpdateScheduler().schedule(neighborPos, pos, direction.getOpposite(), flags, maxUpdateDepth, UpdateType.STATE_UPDATE);
					}
				}

				ci.cancel();
			}
		} else {
			ci.cancel();
		}
	}
}

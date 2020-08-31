package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

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
import redstonetweaks.helper.AbstractBlockHelper;
import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.world.server.ScheduledNeighborUpdate.UpdateType;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

	@Inject(method = "isSideSolidFullSquare", cancellable = true, at = @At(value = "RETURN"))
	private void onIsSideSolidFullSquareInjectAtReturn(BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ()) {
			cir.setReturnValue(AbstractBlockHelper.isRigidPistonBase(world, pos, (BlockState)(Object)this));
			cir.cancel();
		}
	}

	@Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
		if (GLOBAL.get(DO_STATE_UPDATES)) {
			if (((WorldHelper) world).shouldSeparateUpdates()) {
				if (!world.isClient()) {
					for (Direction direction : BlockHelper.getFacings()) {
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

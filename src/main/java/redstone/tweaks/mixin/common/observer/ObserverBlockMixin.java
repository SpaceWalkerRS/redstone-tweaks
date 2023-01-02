package redstone.tweaks.mixin.common.observer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.ObserverOverrides;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin implements ObserverOverrides {

	@Inject(
		method = "updateShape",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtObserveBlockUpdates(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (Tweaks.Observer.observeBlockUpdates()) {
			cir.setReturnValue(state);
		}
	}

	@Inject(
		method = "startSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtDisable(LevelAccessor level, BlockPos pos, CallbackInfo ci) {
		if (Tweaks.Observer.disable()) {
			ci.cancel();
		}
	}

	@ModifyArg(
		method = "onPlace",
		index = 2,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private int rtFixMC136566AndMC137127(int flags) {
		if (Tweaks.BugFixes.MC136566()) {
			flags |= Block.UPDATE_NEIGHBORS;
		}
		if (Tweaks.BugFixes.MC137127()) {
			flags &= ~Block.UPDATE_KNOWN_SHAPE;
		}

		return flags;
	}

	@Override
	public boolean overrideNeighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (level.isClientSide()) {
			return false;
		}
		if (Tweaks.Observer.disable() || !Tweaks.Observer.observeBlockUpdates()) {
			return false;
		}
		if (state.getValue(ObserverBlock.POWERED)) {
			return false;
		}

		if (neighborPos.equals(pos) || neighborPos.equals(pos.relative(state.getValue(ObserverBlock.FACING)))) {
			ObserverOverrides.scheduleOrDoTick(level, pos, state, false);
		}

		return false;
	}
}

package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin {

	private boolean rt_receivingPower;

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Boolean;booleanValue()Z"
		)
	)
	private boolean rtTweakLazy(Boolean powered) {
		return true; // to make sure the lamp will also toggle off on a tick
	}

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity(Level _level, BlockPos _pos, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		boolean lit = state.getValue(RedstoneLampBlock.LIT);
		boolean lazy = lit ? Tweaks.RedstoneLamp.lazyFallingEdge() : Tweaks.RedstoneLamp.lazyRisingEdge();

		QuasiConnectivity qc = Tweaks.RedstoneLamp.quasiConnectivity();
		boolean randQC = Tweaks.RedstoneLamp.randomizeQuasiConnectivity();

		rt_receivingPower = BlockOverrides.hasSignal(level, pos, qc, randQC);

		return lazy ? !lit : rt_receivingPower;
	}

	@Inject(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakLazy(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		boolean wasLit = state.getValue(RedstoneLampBlock.LIT);
		boolean isLit = !wasLit;
		boolean lazy = wasLit ? Tweaks.RedstoneLamp.lazyFallingEdge() : Tweaks.RedstoneLamp.lazyRisingEdge();

		if (lazy && isLit != rt_receivingPower) {
			int delay = Tweaks.RedstoneLamp.delay(isLit);
			TickPriority priority = Tweaks.RedstoneLamp.tickPriority(isLit);

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
		}
	}
}

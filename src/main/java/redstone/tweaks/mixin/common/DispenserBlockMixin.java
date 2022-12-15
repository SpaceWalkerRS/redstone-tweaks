package redstone.tweaks.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.DispenserOverrides;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin implements DispenserOverrides {

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity(Level level, BlockPos pos) {
		Map<Direction, Boolean> qc = quasiConnectivity();
		boolean randQC = randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		// Droppers and dispensers usually schedule their own ticks before updating neighboring blocks.
		// However, in the case where they have 0 delay, and are thus instantaneous, this leads to item dupes.
		// Therefore we schedule the tick after the block state has been set in that case.
		if (delay() != 0) {
			scheduleOrDoTick(level, pos, state);
		}
	}

	@Inject(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			ordinal = 0,
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		if (delay() == 0) {
			scheduleOrDoTick(level, pos, state);
		}
	}

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakLazy(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (!lazy() && !BlockOverrides.hasSignal(level, pos, quasiConnectivity(), randomizeQuasiConnectivity())) {
			ci.cancel();
		}
	}

	private void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay(), tickPriority());
	}

	@Override
	public int delay() {
		return Tweaks.Dispenser.delay();
	}

	@Override
	public boolean lazy() {
		return Tweaks.Dispenser.lazy();
	}

	@Override
	public Map<Direction, Boolean> quasiConnectivity() {
		return Tweaks.Dispenser.quasiConnectivity();
	}

	@Override
	public boolean quasiConnectivity(Direction dir) {
		return Tweaks.Dispenser.quasiConnectivity(dir);
	}

	@Override
	public boolean randomizeQuasiConnectivity() {
		return Tweaks.Dispenser.randomizeQuasiConnectivity();
	}

	@Override
	public TickPriority tickPriority() {
		return Tweaks.Dispenser.tickPriority();
	}
}

package redstone.tweaks.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity(Level level, BlockPos pos) {
		Map<Direction, Boolean> qc = Tweaks.CommandBlock.quasiConnectivity();
		boolean randQC = Tweaks.CommandBlock.randomizeQuasiConnectivity();

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
		scheduleOrDoTick(level, pos, state);
	}

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(ServerLevel _level, BlockPos _pos, Block block, int delay, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		scheduleOrDoTick(level, pos, state);
	}

	private static void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state) {
		int delay = Tweaks.CommandBlock.delay();
		TickPriority priority = Tweaks.CommandBlock.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}
}

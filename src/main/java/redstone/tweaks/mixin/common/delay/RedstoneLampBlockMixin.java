package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin {

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakRisingEdgeDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		delay = Tweaks.RedstoneLamp.delayRisingEdge();
		TickPriority priority = Tweaks.RedstoneLamp.tickPriorityRisingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private boolean rtTweakFallingEdgeDelayAndTickPriority(Level _level, BlockPos _pos, BlockState _state, int flags, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		int delay = Tweaks.RedstoneLamp.delayFallingEdge();

		if (delay == 0) {
			return level.setBlock(pos, _state, flags);
		}

		return BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.RedstoneLamp.tickPriorityFallingEdge());
	}
}

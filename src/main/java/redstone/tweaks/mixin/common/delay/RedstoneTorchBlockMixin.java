package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.RedstoneTorchOverrides;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin implements RedstoneTorchOverrides {

	@Shadow private boolean hasNeighborSignal(Level level, BlockPos pos, BlockState state) { return false; }

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/RedstoneTorchBlock;hasNeighborSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtTweakLazy(RedstoneTorchBlock torch, Level level, BlockPos pos, BlockState state) {
		boolean lit = state.getValue(RedstoneTorchBlock.LIT);
		boolean lazy = Tweaks.RedstoneTorch.lazy(lit);

		return lazy ? lit : hasNeighborSignal(level, pos, state);
	}

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		boolean lit = state.getValue(RedstoneTorchBlock.LIT);

		delay = Tweaks.RedstoneTorch.delay(lit);
		TickPriority priority = Tweaks.RedstoneTorch.tickPriority(lit);

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, Tweaks.RedstoneTorch::microtickMode);
	}

	@Override
	public Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		return BlockOverrides.scheduleOrDoTick(level, pos, state, type, TickPriority.NORMAL, Tweaks.RedstoneTorch::microtickMode);
	}
}

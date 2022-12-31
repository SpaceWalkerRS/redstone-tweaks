package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PistonOverrides;
import redstone.tweaks.util.MotionType;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	private boolean ticking;

	@Shadow private void checkIfExtend(Level level, BlockPos pos, BlockState state) { }
	@Shadow private boolean getNeighborSignal(Level level, BlockPos pos, Direction facing) { return false; }

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;getNeighborSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
		)
	)
	private boolean rtTweakLazy(PistonBaseBlock piston, Level _level, BlockPos _pos, Direction facing, Level level, BlockPos pos, BlockState state) {
		if (ticking) {
			boolean extend = !state.getValue(PistonBaseBlock.EXTENDED);
			boolean lazy = Tweaks.Piston.lazy(extend, isSticky());

			if (lazy) {
				return extend;
			}
		}

		return getNeighborSignal(level, pos, facing);
	}

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block piston, int type, int data, Level level, BlockPos pos, BlockState state) {
		queueBlockEvent(level, pos, state, type, data);
	}

	@Redirect(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;getNeighborSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
		)
	)
	private boolean rtTweakLazy(PistonBaseBlock piston, Level _level, BlockPos _pos, Direction facing, BlockState state, Level level, BlockPos pos, int type, int data) {
		boolean extend = MotionType.isExtend(type);
		boolean lazy = Tweaks.Piston.lazy(extend, isSticky());

		return lazy ? extend : getNeighborSignal(level, pos, facing);
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		checkIfExtend(level, pos, state);
		ticking = false;

		return true;
	}

	@Override
	public void queueBlockEvent(Level level, BlockPos pos, BlockState state, int type, int data) {
		if (ticking) {
			level.blockEvent(pos, block(), type, data);
		} else {
			boolean extend = MotionType.isExtend(type);

			int delay = Tweaks.Piston.delay(extend, isSticky());
			TickPriority priority = Tweaks.Piston.tickPriority(extend, isSticky());

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
		}
	}
}

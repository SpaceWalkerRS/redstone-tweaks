package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
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
public abstract class DispenserBlockMixin implements DispenserOverrides {

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

	private void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay(), tickPriority());
	}

	@Override
	public int delay() {
		return Tweaks.Dispenser.delay();
	}

	@Override
	public TickPriority tickPriority() {
		return Tweaks.Dispenser.tickPriority();
	}
}

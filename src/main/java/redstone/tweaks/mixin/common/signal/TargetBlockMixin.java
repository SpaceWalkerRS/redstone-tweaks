package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.util.Directions;

@Mixin(TargetBlock.class)
public class TargetBlockMixin implements BlockOverrides {

	@Shadow private static IntegerProperty OUTPUT_POWER;

	@Inject(
		method = "setOutputPower",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private static void rtTweakEmitDirectSignal(LevelAccessor levelAccessor, BlockState state, int power, BlockPos pos, int delay, CallbackInfo ci) {
		if (Tweaks.Target.emitDirectSignal() && levelAccessor instanceof Level) {
			Level level = (Level)levelAccessor;

			for (Direction dir : Directions.ALL) {
				level.updateNeighborsAt(pos.relative(dir), state.getBlock());
			}
		}
	}

	@Override
	public Integer overrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		if (Tweaks.Target.emitDirectSignal()) {
			return state.getValue(OUTPUT_POWER);
		}

		return null;
	}
}

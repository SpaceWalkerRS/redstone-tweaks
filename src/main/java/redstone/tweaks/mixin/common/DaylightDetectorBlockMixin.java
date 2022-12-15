package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(DaylightDetectorBlock.class)
public class DaylightDetectorBlockMixin implements BlockOverrides {

	@Override
	public Integer overrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		return Tweaks.DaylightDetector.emitDirectSignal() ? state.getValue(DaylightDetectorBlock.POWER) : null;
	}
}

package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements DiodeOverrides {

	@Override
	public boolean invertAlternateSignal(BlockState state) {
		return false;
	}

	@Override
	public int signal(BlockGetter level, BlockPos pos, BlockState state) {
		return Tweaks.Repeater.signal();
	}

	@Override
	public int signalDirect(BlockGetter level, BlockPos pos, BlockState state) {
		return Tweaks.Repeater.signalDirect();
	}
}

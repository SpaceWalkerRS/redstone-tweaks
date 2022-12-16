package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface DiodeOverrides extends BlockOverrides {

	boolean invertAlternateSignal(BlockState state);

	boolean microtickMode();

	int signal(BlockGetter level, BlockPos pos, BlockState state);

	int signalDirect(BlockGetter level, BlockPos pos, BlockState state);

}

package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface RedstoneOreOverrides extends BlockOverrides {

	boolean interact(Level level, BlockPos pos, BlockState state);

}

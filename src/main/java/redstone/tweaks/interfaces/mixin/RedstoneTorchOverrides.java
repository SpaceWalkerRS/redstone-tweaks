package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface RedstoneTorchOverrides extends BlockOverrides {

	Direction getFacing(BlockState state);

	boolean requestDirectSignal();

}

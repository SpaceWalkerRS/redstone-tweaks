package redstone.tweaks.interfaces.mixin;

import net.minecraft.world.level.block.state.BlockState;

public interface DiodeOverrides extends BlockOverrides {

	boolean invertAlternateSignal(BlockState state);

}

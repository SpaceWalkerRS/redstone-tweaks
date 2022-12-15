package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public interface ILevel {

	boolean hasBlockEvent(BlockPos pos, Block block);

}

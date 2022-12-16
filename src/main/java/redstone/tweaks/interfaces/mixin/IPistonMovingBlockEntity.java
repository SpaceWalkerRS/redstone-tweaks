package redstone.tweaks.interfaces.mixin;

import net.minecraft.world.level.block.Block;

public interface IPistonMovingBlockEntity {

	void init(Block source);

	float getAmountPerStep();

}

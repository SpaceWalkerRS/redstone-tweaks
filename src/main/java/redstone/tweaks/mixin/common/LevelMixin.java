package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import redstone.tweaks.interfaces.mixin.ILevel;

@Mixin(Level.class)
public class LevelMixin implements ILevel {

	@Override
	public boolean hasBlockEvent(BlockPos pos, Block block) {
		return false;
	}
}

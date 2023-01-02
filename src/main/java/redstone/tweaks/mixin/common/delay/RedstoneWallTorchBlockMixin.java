package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.RedstoneTorchOverrides;

@Mixin(RedstoneWallTorchBlock.class)
public abstract class RedstoneWallTorchBlockMixin implements RedstoneTorchOverrides {

	@Override
	public Direction getFacing(BlockState state) {
		return state.getValue(RedstoneWallTorchBlock.FACING);
	}
}

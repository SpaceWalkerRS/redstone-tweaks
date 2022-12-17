package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(MovingPistonBlock.class)
public class MovingPistonBlockMixin implements BlockOverrides {

	@Override
	public PushReaction overrideGetPistonPushReaction(BlockState state) {
		if (Tweaks.Global.movableMovingBlocks()) {
			return PushReaction.NORMAL;
		}

		return null;
	}
}

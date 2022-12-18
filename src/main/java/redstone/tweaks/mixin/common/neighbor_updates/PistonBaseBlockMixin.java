package redstone.tweaks.mixin.common.neighbor_updates;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@ModifyArg(
		method = "moveBlocks",
		index = 2,
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private int rtEnableRetractionNeighborUpdatesAroundHead(int flags) {
		if (Tweaks.Piston.headUpdatesNeighborsOnRetraction(isSticky())) {
			flags |= Block.UPDATE_NEIGHBORS;
			flags &= ~Block.UPDATE_KNOWN_SHAPE;
		}

		return flags;
	}

	@Redirect(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;updateNeighborsAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"
		)
	)
	private void rtSuppressExtensionNeighborUpdatesAroundHead(Level level, BlockPos pos, Block block) {
		if (Tweaks.Piston.headUpdatesNeighborsOnExtension(isSticky())) {
			level.updateNeighborsAt(pos, block);
		}
	}
}

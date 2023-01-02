package redstone.tweaks.mixin.common.neighbor_updates;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

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

	@Inject(
		method = "moveBlocks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/Level;updateNeighborsAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"
		)
	)
	private void rtFixMC120986(Level level, BlockPos pos, Direction facing, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos headPos, PistonStructureResolver structureResolver, Map<BlockPos, BlockState> leftOverStates, List<BlockPos> toMove, List<BlockState> statesToMove, List<BlockPos> toDestroy, BlockState[] affectedStates, int affectedIndex, int movedIndex) {
		if (Tweaks.BugFixes.MC120986()) {
			BlockPos movedPos = toMove.get(movedIndex);
			BlockState movedState = affectedStates[affectedIndex - 1];

			if (movedState.hasAnalogOutputSignal()) {
				level.updateNeighbourForOutputSignal(movedPos, movedState.getBlock());
			}
		}
	}
}

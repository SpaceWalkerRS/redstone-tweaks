package redstone.tweaks.mixin.common.wire_connections;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {

	@Shadow private static boolean shouldConnectTo(BlockState state, Direction side) { return false; }

	@Redirect(
		method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;",
		at = @At(
			value = "FIELD",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/block/state/properties/RedstoneSide;SIDE:Lnet/minecraft/world/level/block/state/properties/RedstoneSide;"
		)
	)
	private RedstoneSide rtTweakSlabsAllowUpConnection() {
		return Tweaks.RedstoneWire.slabsAllowUpConnection() ? RedstoneSide.SIDE : RedstoneSide.NONE;
	}

	@Redirect(
		method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"
		)
	)
	private boolean rtTweakConnecToWire(BlockState state, Direction _side, BlockGetter level, BlockPos pos, Direction side, boolean allowUpConnection) {
		return shouldConnectTo(state, side) || shouldConnectTo(level, pos, side, pos.relative(side), state);
	}

	@Inject(
		method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void rtTweakConnectToWire(BlockState state, Direction side, CallbackInfoReturnable<Boolean> cir) {
		// side is null for diagonal connections
		if (side != null) {
			if (PistonOverrides.isBase(state)) {
				boolean isSticky = PistonOverrides.isBaseSticky(state);

				if (Tweaks.Piston.connectToWire(isSticky)) {
					cir.setReturnValue(true);
				}
			}
			if (state.is(Blocks.RED_SAND)) {
				cir.setReturnValue(Tweaks.RedSand.connectToWire());
			}
			if (state.is(Blocks.REDSTONE_ORE) || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)) {
				cir.setReturnValue(Tweaks.RedstoneOre.connectToWire());
			}
		}
	}

	private boolean shouldConnectTo(BlockGetter level, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighborState) {
		if (neighborState.is(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = level.getBlockEntity(neighborPos);

			if (blockEntity instanceof PistonMovingBlockEntity) {
				PistonMovingBlockEntity mbe = (PistonMovingBlockEntity)blockEntity;

				if (mbe.isSourcePiston() && !mbe.isExtending()) {
					BlockState movedState = mbe.getMovedState();

					if (PistonOverrides.isBase(movedState)) {
						return Tweaks.Piston.connectToWire(PistonOverrides.isBaseSticky(movedState));
					}
				}
			}
		}

		return false;
	}
}

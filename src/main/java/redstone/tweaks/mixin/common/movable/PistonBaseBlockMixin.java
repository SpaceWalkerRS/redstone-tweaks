package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Shadow private boolean isSticky;

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/piston/PistonStructureResolver;"
		)
	)
	private PistonStructureResolver rtNewStructureResolver1(Level level, BlockPos pos, Direction facing, boolean extending) {
		return PistonOverrides.newStructureResolver(this, level, pos, facing, extending);
	}

	@Inject(
		method = "isPushable",
		cancellable = true,
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;EXTENDED:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;"
			)
		),
		at = @At(
			value = "RETURN",
			ordinal = 0
		)
	)
	private static void rtTweakPistonMovable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (isMovable(state)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(
		method = "isPushable",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
		)
	)
	private static void rtTweakBarrierMovable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(Blocks.BARRIER) && Tweaks.Barrier.movable()) {
			cir.setReturnValue(true);
		}
	}

	@Redirect(
		method = "isPushable",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"
		)
	)
	private static PushReaction rtTweakHayMovable(BlockState _state, BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
		if (state.is(Blocks.HAY_BLOCK) && Tweaks.Hay.blockMisalignedPistonMove()) {
			if (state.getValue(HayBlock.AXIS) != moveDir.getAxis()) {
				return PushReaction.BLOCK;
			}
		}

		return state.getPistonPushReaction();
	}

	@Redirect(
		method = "moveBlocks",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/piston/PistonStructureResolver;"
		)
	)
	private PistonStructureResolver rtNewStructureResolver2(Level level, BlockPos pos, Direction facing, boolean extending) {
		return PistonOverrides.newStructureResolver(this, level, pos, facing, extending);
	}

	private static boolean isMovable(BlockState state) {
		boolean isSticky = ((PistonOverrides)state.getBlock()).isSticky();

		if (Tweaks.Piston.looseHead(isSticky)) {
			return true;
		}
		if (Tweaks.Piston.movableWhenExtended(isSticky)) {
			return true;
		}

		return false;
	}
}

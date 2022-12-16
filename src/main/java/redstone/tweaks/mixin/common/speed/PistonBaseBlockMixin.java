package redstone.tweaks.mixin.common.speed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Redirect(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private BlockEntity rtNewMovingBlockEntity1(BlockPos pos, BlockState state, BlockState movedState, Direction facing, boolean extending, boolean isSourcePiston) {
		return PistonOverrides.newMovingBlockEntity(this, pos, state, movedState, facing, extending, isSourcePiston);
	}

	@Redirect(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private BlockEntity rtNewMovingBlockEntity2(BlockPos pos, BlockState state, BlockState movedState, Direction facing, boolean extending, boolean isSourcePiston) {
		return PistonOverrides.newMovingBlockEntity(this, pos, state, movedState, facing, extending, isSourcePiston);
	}
}

package redstone.tweaks.mixin.common.block_dropping;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.IPistonMovingBlockEntity;
import redstone.tweaks.interfaces.mixin.IPistonStructureResolver;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin implements IPistonStructureResolver {

	@Shadow @Final Level level;
	@Shadow @Final Direction pushDirection;
	@Shadow @Final boolean extending;

	private boolean resolveMoving;

	@Shadow private boolean resolve() { return false; }

	@Redirect(
		method = "resolve",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private BlockState rtResolveMovingBlockState1(Level level, BlockPos pos) {
		return resolveMoving ? getMovingBlockState(pos) : level.getBlockState(pos);
	}

	@Redirect(
		method = "addBlockLine",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private BlockState rtResolveMovingBlockState2(Level level, BlockPos pos) {
		return resolveMoving ? getMovingBlockState(pos) : level.getBlockState(pos);
	}

	@Redirect(
		method = "addBranchingBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private BlockState rtResolveMovingBlockState3(Level level, BlockPos pos) {
		return resolveMoving ? getMovingBlockState(pos) : level.getBlockState(pos);
	}

	@Override
	public boolean resolveMoving() {
		resolveMoving = true;
		return resolve();
	}

	private BlockState getMovingBlockState(BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (!state.is(Blocks.MOVING_PISTON)) {
			return Blocks.AIR.defaultBlockState();
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (!(blockEntity instanceof PistonMovingBlockEntity)) {
			return Blocks.AIR.defaultBlockState();
		}

		PistonMovingBlockEntity mbe = (PistonMovingBlockEntity)blockEntity;

		if (mbe.isExtending() != extending || mbe.getDirection() != pushDirection) {
			return Blocks.AIR.defaultBlockState();
		}

		return ((IPistonMovingBlockEntity)mbe).recurseMovedState();
	}
}

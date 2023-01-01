package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import redstone.tweaks.RedstoneTweaksMod;
import redstone.tweaks.interfaces.mixin.ILevel;
import redstone.tweaks.interfaces.mixin.IPistonMovingBlockEntity;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin extends BlockEntity implements IPistonMovingBlockEntity {

	private static final String NBT_KEY_MOVED_BLOCKENTITY = "rt_movedBlockEntity";

	@Shadow private Direction direction;
	@Shadow private float progress;
	@Shadow private BlockState movedState;

	private BlockEntity movedBlockEntity;
	private PistonMovingBlockEntity parent;

	private PistonMovingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Shadow private float getExtendedProgress(float progress) { return 0.0F; }

	@Inject(
		method = "finalTick",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private void rtPlaceMovedBlock(CallbackInfo ci) {
		if (prepareMovedBlockPlacement()) {
			ci.cancel();
		}
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private static void rtTickMovedBlock(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity movingBlockEntity, CallbackInfo ci) {
		((IPistonMovingBlockEntity)movingBlockEntity).tickMovedBlockEntity();
	}

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private static void rtPlaceMovedBlock(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity movingBlockEntity, CallbackInfo ci) {
		if (((IPistonMovingBlockEntity)movingBlockEntity).prepareMovedBlockPlacement()) {
			ci.cancel();
		}
	}

	@Inject(
		method = "load",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtLoadNbt(CompoundTag nbt, CallbackInfo ci) {
		if (movedState.hasBlockEntity() && nbt.contains(NBT_KEY_MOVED_BLOCKENTITY)) {
			BlockEntity blockEntity = null;

			if (movedState.is(Blocks.MOVING_PISTON)) {
				blockEntity = new PistonMovingBlockEntity(worldPosition, movedState);
			} else {
				blockEntity = ((EntityBlock)movedState.getBlock()).newBlockEntity(worldPosition, movedState);
			}

			blockEntity.load(nbt.getCompound(NBT_KEY_MOVED_BLOCKENTITY));

			setMovedBlock(movedState, blockEntity);
		}
	}

	@Inject(
		method = "saveAdditional",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtSaveNbt(CompoundTag nbt, CallbackInfo ci) {
		if (movedBlockEntity != null) {
			nbt.put(NBT_KEY_MOVED_BLOCKENTITY, movedBlockEntity.saveWithoutMetadata());
		}
	}

	@Inject(
		method = "getCollisionShape",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtGetCollisionShape(BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
		if (movedState.is(Blocks.MOVING_PISTON)) {
			VoxelShape shape = recurseMovingBlock().getCollisionShape(level, pos);

			float extendedProgress = getExtendedProgress(progress);
			double dx = direction.getStepX() * extendedProgress;
			double dy = direction.getStepY() * extendedProgress;
			double dz = direction.getStepZ() * extendedProgress;

			cir.setReturnValue(shape.move(dx, dy, dz));
		}
	}

	@Override
	public PistonMovingBlockEntity recurseMovingBlock() {
		if (movedState.is(Blocks.MOVING_PISTON)) {
			if (movedBlockEntity == null) {
				RedstoneTweaksMod.LOGGER.warn("moving block is moved but missing its moving block entity!");
				new Exception().printStackTrace();

				return new PistonMovingBlockEntity(worldPosition, Blocks.AIR.defaultBlockState());
			} else {
				return ((IPistonMovingBlockEntity)movedBlockEntity).recurseMovingBlock();
			}
		} else {
			return (PistonMovingBlockEntity)(Object)this;
		}
	}

	@Override
	public void setMovedBlock(BlockState state, BlockEntity blockEntity) {
		this.movedState = state;
		this.movedBlockEntity = blockEntity;

		if (this.movedBlockEntity instanceof PistonMovingBlockEntity) {
			((IPistonMovingBlockEntity)this.movedBlockEntity).setParent((PistonMovingBlockEntity)(Object)this);
		}
	}

	@Override
	public BlockEntity getMovedBlockEntity() {
		return movedBlockEntity;
	}

	@Override
	public BlockState recurseMovedState() {
		return recurseMovingBlock().getMovedState();
	}

	@Override
	public BlockEntity recurseMovedBlockEntity() {
		return ((IPistonMovingBlockEntity)recurseMovingBlock()).getMovedBlockEntity();
	}

	@Override
	public void tickMovedBlockEntity() {
		if (movedBlockEntity != null) {
			tickBlockEntity(level, worldPosition, movedState, movedBlockEntity);
		}
	}

	@Override
	public boolean prepareMovedBlockPlacement() {
		if (parent == null) {
			if (movedBlockEntity != null) {
				((ILevel)level).prepareMovedBlockEntityPlacement(worldPosition, movedState, movedBlockEntity);
			}
		} else {
			((IPistonMovingBlockEntity)parent).setMovedBlock(movedState, movedBlockEntity);
		}

		return parent != null;
	}

	@Override
	public PistonMovingBlockEntity getParent() {
		return parent;
	}

	@Override
	public void setParent(PistonMovingBlockEntity movingBlockEntity) {
		parent = movingBlockEntity;
	}

	private static <T extends BlockEntity> void tickBlockEntity(Level level, BlockPos pos, BlockState state, T blockEntity) {
		@SuppressWarnings("unchecked")
		BlockEntityTicker<T> ticker = (BlockEntityTicker<T>)state.getTicker(level, blockEntity.getType());

		if (ticker != null) {
			ticker.tick(level, pos, state, blockEntity);
		}
	}
}

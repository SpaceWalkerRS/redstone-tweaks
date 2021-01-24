package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import redstonetweaks.interfaces.mixin.RTIWorld;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
	
	@Shadow @Final private World world;
	
	@Shadow public abstract void setBlockEntity(BlockPos pos, BlockEntity blockEntity);
	@Shadow public abstract BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType);
	@Shadow public abstract void removeBlockEntity(BlockPos pos);
	@Shadow protected abstract BlockEntity createBlockEntity(BlockPos pos);
	
	@Inject(method = "setBlockState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "FIELD", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/world/World;isClient:Z"))
	private void onSetBlockStateInjectBeforeIsClient1(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir,
			int chunkX, int chunkY, int chunkZ, ChunkSection chunkSection, boolean sectionIsEmpty, BlockState oldState, Block newBlock) 
	{
		if (newBlock.hasBlockEntity()) {
			BlockEntity blockEntity = ((RTIWorld)world).fetchQueuedBlockEntity(pos);
			
			if (blockEntity != null) {
				world.removeBlockEntity(pos);
				removeBlockEntity(pos);
				
				blockEntity.cancelRemoval();
				blockEntity.setLocation(world, pos);
				
				// We don't set the block entity in the chunk while block entities are ticking
				// because it might get marked removed immediately.
				world.setBlockEntity(pos, blockEntity);
				if (!((RTIWorld)world).isTickingBlockEntities()) {
					setBlockEntity(pos, blockEntity);
				}
			}
		}
	}
	
	@Redirect(method = "setBlockState", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onSetBlockStateRedirectGetBlockEntity(WorldChunk chunk, BlockPos blockPos, WorldChunk.CreationType creationType, BlockPos pos, BlockState state, boolean moved) {
		// We don't set the block entity in the chunk while block entities are ticking
		// because it might get marked removed immediately. Therefore we check if there is a
		// block entity in the world instead.
		return ((RTIWorld)world).isTickingBlockEntities() ? world.getBlockEntity(pos) : chunk.getBlockEntity(pos, creationType);
	}
}

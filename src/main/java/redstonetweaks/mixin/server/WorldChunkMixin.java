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
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import redstonetweaks.interfaces.RTIWorld;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
	
	@Shadow @Final private World world;
	
	private boolean sameBlock;
	
	@Inject(method = "setBlockState", locals = LocalCapture.CAPTURE_FAILHARD, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V")), at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/chunk/WorldChunk;getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;"))
	private void onSetBlockStateInjectBeforeGetBlockEntity(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir, int i, int j, int k, ChunkSection chunkSection, boolean chunkSectionEmpty, BlockState oldState, Block newBlock) {
		sameBlock = oldState.isOf(newBlock);
	}
	
	@Redirect(method = "setBlockState", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onSetBlockStateRedirectGetBlockEntity(WorldChunk chunk, BlockPos blockPos, WorldChunk.CreationType creationType, BlockPos pos, BlockState state, boolean moved) {
		if (sameBlock) {
			sameBlock = false;
			
			if (((RTIWorld)world).isTickingBlockEntities()) {
				return world.getBlockEntity(pos);
			}
		}
		return chunk.getBlockEntity(pos, creationType);
	}
	
	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockEntityProvider;createBlockEntity(Lnet/minecraft/world/BlockView;)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onSetBlockStateRedirectCreateBlockEntity(BlockEntityProvider block, BlockView world, BlockPos pos, BlockState state, boolean moved) {
		BlockEntity movedBlockEntity = ((RTIWorld)this.world).getMovedBlockEntity();
		
		return movedBlockEntity == null ? block.createBlockEntity(world) : movedBlockEntity;
	}
}

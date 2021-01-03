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

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import redstonetweaks.mixinterfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
	
	@Shadow @Final private World world;
	
	@Shadow public abstract void setBlockEntity(BlockPos pos, BlockEntity blockEntity);
	@Shadow public abstract BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType);
	@Shadow public abstract void removeBlockEntity(BlockPos pos);
	@Shadow protected abstract BlockEntity createBlockEntity(BlockPos pos);
	
	@Inject(method = "setBlockState", at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private void onSetBlockStateInjectBeforeGetBlock0(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
		if (moved && Tweaks.Global.INSTANT_BLOCK_EVENTS.get() && !world.isClient() && !state.isAir() && !state.isOf(Blocks.MOVING_PISTON)) {
			BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
			((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
		}
	}
	
	@Inject(method = "setBlockState", at = @At(value = "FIELD", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/world/World;isClient:Z"))
	private void onSetBlockStateInjectBeforeIsClient1(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
		BlockEntity blockEntity = ((RTIWorld)world).fetchQueuedBlockEntity();
		
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
	
	@Redirect(method = "setBlockState", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onSetBlockStateRedirectGetBlockEntity(WorldChunk chunk, BlockPos blockPos, WorldChunk.CreationType creationType, BlockPos pos, BlockState state, boolean moved) {
		// We don't set the block entity in the chunk while block entities are ticking
		// because it might get marked removed immediately. Therefore we check if there is a
		// block entity in the world instead.
		return ((RTIWorld)world).isTickingBlockEntities() ? world.getBlockEntity(pos) : chunk.getBlockEntity(pos, creationType);
	}
}

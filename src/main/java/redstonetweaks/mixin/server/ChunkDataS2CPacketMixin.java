package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;

import redstonetweaks.block.entity.BlockEntityTypes;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkDataS2CPacketMixin {
	
	// We need to send all the data for some block entities so that redstone wire, comparators,
	// target blocks, etc. can display their signal strength in the F3 screen
	@Redirect(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;toInitialChunkDataTag()Lnet/minecraft/nbt/CompoundTag;"))
	private CompoundTag onInitRedirectToInitialChunkDataTag(BlockEntity blockEntity) {
		if (BlockEntityTypes.hasId(blockEntity.getType())) {
			return blockEntity.toTag(new CompoundTag());
		}
		
		return blockEntity.toInitialChunkDataTag();
	}
}

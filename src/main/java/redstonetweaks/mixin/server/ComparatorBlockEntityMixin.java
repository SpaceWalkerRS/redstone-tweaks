package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

import redstonetweaks.block.entity.BlockEntityTypes;

@Mixin(ComparatorBlockEntity.class)
public abstract class ComparatorBlockEntityMixin extends BlockEntity {
	
	public ComparatorBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(method = "setOutputSignal", at = @At(value = "RETURN"))
	private void onSetOutputSignalInjectAtHead(int newPower, CallbackInfo ci) {
		if (!world.isClient() && newPower > 15) {
			world.getServer().getPlayerManager().sendToAround(null, getPos().getX(), getPos().getY(), getPos().getZ(), 64.0D, world.getRegistryKey(), toUpdatePacket());
		}
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTag(new CompoundTag());
	}
	
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(pos, BlockEntityTypes.getId(getType()), toTag(new CompoundTag()));
	}
}

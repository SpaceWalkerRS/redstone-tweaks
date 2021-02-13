package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Properties;

import redstonetweaks.block.entity.BlockEntityTypes;
import redstonetweaks.interfaces.mixin.RTIDaylightDetectorBlockEntity;

@Mixin(DaylightDetectorBlockEntity.class)
public abstract class DaylightDetectorBlockEntityMixin extends BlockEntity implements RTIDaylightDetectorBlockEntity {
	
	private int power;
	private boolean powerCorrected;
	
	public DaylightDetectorBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		
		tag.putInt("Power", power);
		
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		
		power = tag.getInt("Power");
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTag(new CompoundTag());
	}
	
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(pos, BlockEntityTypes.getId(getType()), toTag(new CompoundTag()));
	}
	
	@Override
	public void setPower(int newPower) {
		power = newPower;
	}
	
	@Override
	public int getPower() {
		return power;
	}

	// If the world was loaded in vanilla there will not be any block entity data but there might still be
	// powered redstone components. In that case a new block entity is created and given a default power value
	// of 0. In the case where the block entity power is 0 but the power level in the block state is not,
	// we set the block entity power level to the block state power level
	@Override
	public void ensureCorrectPower(BlockState state) {
		if (!powerCorrected || getPower() == 0) {
			setPower(state.get(Properties.POWER));
			powerCorrected = true;
		}
	}
}

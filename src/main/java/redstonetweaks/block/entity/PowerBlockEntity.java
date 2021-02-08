package redstonetweaks.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Properties;

public class PowerBlockEntity extends BlockEntity {
	
	private int power;
	private boolean powerCorrected;
	
	public PowerBlockEntity() {
		super(BlockEntityTypes.POWER_BLOCK);
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
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(pos, BlockEntityTypes.getId(getType()), toTag(new CompoundTag()));
	}
	
	public int getPower() {
		return power;
	}
	
	public void setPower(int newPower) {
		power = newPower;
	}
	
	// If the world was loaded in vanilla there will not be any block entity data but there might still be
	// powered redstone components. In that case a new block entity is created and given a default power value
	// of 0. In the case where the block entity power is 0 but the power level in the block state is not,
	// we set the block entity power level to the block state power level
	public void ensureCorrectPower(BlockState state) {
		if ((!powerCorrected || getPower() == 0) && getType().supports(state.getBlock())) {
			setPower(state.get(Properties.POWER));
			powerCorrected = true;
		}
	}
}

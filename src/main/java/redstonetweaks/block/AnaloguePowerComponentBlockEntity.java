package redstonetweaks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.helper.BlockEntityHelper;

public class AnaloguePowerComponentBlockEntity extends BlockEntity {
	
	private int power;
	
	public AnaloguePowerComponentBlockEntity() {
		super(RedstoneTweaks.REDSTONE_POWER);
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
		return new BlockEntityUpdateS2CPacket(pos, BlockEntityHelper.getId(getType()), toTag(new CompoundTag()));
	}
	
	public int getPower() {
		return power;
	}
	
	public void setPower(int newPower) {
		power = newPower;
	}
}

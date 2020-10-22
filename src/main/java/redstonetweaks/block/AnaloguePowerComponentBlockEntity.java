package redstonetweaks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.helper.BlockEntityHelper;
import redstonetweaks.helper.ServerWorldHelper;

public class AnaloguePowerComponentBlockEntity extends BlockEntity implements BlockEntityHelper {
	
	// 1-14 are used by vanilla block entities
	// In case they add more, just make the number sufficiently large
	private static final int ID = 101;
	
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
	public int getId() {
		return ID;
	}
	
	public int getPower() {
		return power;
	}
	
	public void setPower(int newPower) {
		power = newPower;
		
		if (!world.isClient()) {
			((ServerWorldHelper)world).markForBlockEntityUpdate(pos);
		}
	}
}

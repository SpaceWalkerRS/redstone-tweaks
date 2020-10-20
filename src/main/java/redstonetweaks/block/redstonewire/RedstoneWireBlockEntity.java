package redstonetweaks.block.redstonewire;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.helper.BlockEntityHelper;
import redstonetweaks.helper.ServerWorldHelper;

public class RedstoneWireBlockEntity extends BlockEntity implements BlockEntityHelper {
	
	// 1-14 are used by vanilla block entities
	// In case they add more, just make the number sufficiently large
	private static final int ID = 1001;
	
	private int power;
	
	public RedstoneWireBlockEntity() {
		super(RedstoneTweaks.REDSTONE_WIRE);
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

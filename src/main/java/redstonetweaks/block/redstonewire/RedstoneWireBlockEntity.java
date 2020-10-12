package redstonetweaks.block.redstonewire;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

import redstonetweaks.RedstoneTweaks;

public class RedstoneWireBlockEntity extends BlockEntity {
	
	private int power;
	
	public RedstoneWireBlockEntity() {
		super(RedstoneTweaks.REDSTONE_WIRE);
	}
	
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putInt("Power", power);
		return tag;
	}

	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		power = tag.getInt("Power");
	}

	public int getPower() {
		return power;
	}

	public void setPower(int newPower) {
		power = newPower;
	}
}

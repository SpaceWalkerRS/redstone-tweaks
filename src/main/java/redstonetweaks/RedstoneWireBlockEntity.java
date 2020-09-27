package redstonetweaks;

import com.mojang.datafixers.types.Type;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class RedstoneWireBlockEntity extends BlockEntity {
	
	private static final BlockEntityType<RedstoneWireBlockEntity> REDSTONE_WIRE = create("redstone_wire", BlockEntityType.Builder.create(RedstoneWireBlockEntity::new, Blocks.REDSTONE_WIRE));
	
	private int power;
	
	public RedstoneWireBlockEntity() {
		this(REDSTONE_WIRE);
	}
	
	public RedstoneWireBlockEntity(BlockEntityType<?> type) {
		super(type);
	}
	
	private static <T extends BlockEntity> BlockEntityType<T> create(String string, BlockEntityType.Builder<T> builder) {
		Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, string);
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, string, builder.build(type));
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

package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import redstone.tweaks.interfaces.mixin.IBlockEntity;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements IBlockEntity {

	@Shadow @Final @Mutable private BlockPos worldPosition;

	@Override
	public void setPos(BlockPos pos) {
		this.worldPosition = pos;
	}
}

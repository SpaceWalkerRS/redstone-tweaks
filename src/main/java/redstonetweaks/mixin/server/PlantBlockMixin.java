package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIPlant;

@Mixin(PlantBlock.class)
public class PlantBlockMixin implements RTIPlant {
	
	@Redirect(
			method = "canPlaceAt",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/WorldView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
			)
	)
	private BlockState onCanPlaceAtRedirectGetBlockState(WorldView world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity != null && blockEntity instanceof PistonBlockEntity) {
				return ((RTIPistonBlockEntity)blockEntity).getMovedMovingState();
			}
		}
		
		return state;
	}
	
	@Override
	public boolean hasAttachmentTo(BlockState state, Direction dir, Block neighborBlock) {
		return dir == Direction.DOWN;
	}
}

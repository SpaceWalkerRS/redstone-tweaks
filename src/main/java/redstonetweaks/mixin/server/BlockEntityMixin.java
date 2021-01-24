package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
	
	@Shadow private World world;
	
	@Redirect(method = "markDirty", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onMarkDirtyRedirectGetBlockState(World world, BlockPos pos) {
		return getStateForCaching(pos);
	}
	
	@Redirect(method = "getCachedState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onGetCachedStateRedirectGetBlockState(World world, BlockPos pos) {
		return getStateForCaching(pos);
	}
	
	private BlockState getStateForCaching(BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (state.isOf(Blocks.MOVING_PISTON) && !((BlockEntity)(Object)this instanceof PistonBlockEntity)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				state = ((RTIPistonBlockEntity)blockEntity).getMovedMovingState();
			}
		}
		
		return state;
	}
}

package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.interfaces.mixin.RTIBlock;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;

@Mixin(Block.class)
public class BlockMixin implements RTIBlock {
	
	@Redirect(
			method = "postProcessState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/WorldAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
			)
	)
	private static BlockState onPostProcessStateRedirectGetBlockState(WorldAccess world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity != null && blockEntity instanceof PistonBlockEntity) {
				return ((RTIPistonBlockEntity)blockEntity).getMovedMovingState();
			}
		}
		
		return state;
	}
	
	@Inject(
			method = "hasTopRim",
			cancellable =  true,
			at = @At(
					value = "HEAD"
			)
	)
	private static void onHasTopTimInjectAtReturn(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (BlockHelper.isSolidPiston(world, pos, world.getBlockState(pos), Direction.UP)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
}

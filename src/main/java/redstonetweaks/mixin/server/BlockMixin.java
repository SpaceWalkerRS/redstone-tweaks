package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.mixinterfaces.RTIBlock;

@Mixin(Block.class)
public class BlockMixin implements RTIBlock {
	
	@Inject(method = "hasTopRim", cancellable =  true, at = @At(value = "HEAD"))
	private static void onHasTopTimInjectAtReturn(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (BlockHelper.isSolidPiston(world, pos, world.getBlockState(pos), Direction.UP)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		return false;
	}
}

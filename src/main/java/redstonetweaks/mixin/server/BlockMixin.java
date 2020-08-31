package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import redstonetweaks.helper.BlockHelper;

@Mixin(Block.class)
public class BlockMixin {
	
	@Inject(method = "hasTopRim", cancellable =  true, at = @At(value = "RETURN"))
	private static void onHasTopTimInjectAtReturn(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ()) {
			cir.setReturnValue(BlockHelper.isRigidPistonBase(world, pos, world.getBlockState(pos)));
			cir.cancel();
		}
	}
}

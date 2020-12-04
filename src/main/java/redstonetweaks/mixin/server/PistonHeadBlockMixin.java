package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;

@Mixin(PistonHeadBlock.class)
public abstract class PistonHeadBlockMixin {
	
	@Shadow protected abstract boolean method_26980(BlockState blockState, BlockState blockState2);
	
	@Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonHeadBlock;method_26980(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onOnStateRepacedRedirectMethod_26980(PistonHeadBlock pistonHead, BlockState blockState1, BlockState blockState2, BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		return method_26980(blockState1, blockState2) && !(moved && PistonHelper.movableWhenExtended(state.get(Properties.PISTON_TYPE) == PistonType.STICKY));
	}
}

package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.interfaces.mixin.RTIRedstoneTorch;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(WallRedstoneTorchBlock.class)
public abstract class WallRedstoneTorchBlockMixin implements RTIRedstoneTorch {
	
	@Inject(
			method = "shouldUnpower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void shouldUnpower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		Direction attached = state.get(Properties.HORIZONTAL_FACING).getOpposite();
		BlockPos blockPos = pos.offset(attached);
		
		// If the softInversion setting is enabled,
		// return true if the torch is attached to a piston that is
		// receiving redstone power.
		if (Tweaks.RedstoneTorch.SOFT_INVERSION.get()) {
			BlockState blockState = world.getBlockState(blockPos);
			
			if (PistonHelper.isPiston(blockState)) {
				if (PistonHelper.isReceivingPower(world, blockPos, blockState)) {
					cir.setReturnValue(true);
					cir.cancel();
				}
			}
		}
	}
	
	@Inject(
			method = "getWeakRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(((RTIRedstoneTorch)this).getPowerOutput(world, pos, state, dir, false));
		cir.cancel();
	}
	
	@Override
	public Direction getFacing(BlockState state) {
		return state.get(Properties.HORIZONTAL_FACING);
	}
}

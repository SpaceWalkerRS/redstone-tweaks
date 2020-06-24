package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.softInversion;
import static redstonetweaks.setting.Settings.redstoneTorchSignal;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.helper.PistonHelper;

@Mixin(WallRedstoneTorchBlock.class)
public class WallRedstoneTorchBlockMixin {
	
	@Shadow @Final private static DirectionProperty FACING;
	
	@Inject(method = "shouldUnpower", at = @At(value = "HEAD"), cancellable = true)
	private void shouldUnpower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		Direction attached = state.get(FACING).getOpposite();
		BlockPos blockPos = pos.offset(attached);
		
		// If the pistonsPowerRedstoneTorches setting is enabled,
		// return true if the torch is attached to a piston that is
		// receiving redstone power.
		if (softInversion.get()) {
			BlockState blockState = world.getBlockState(blockPos);
			
			if (blockState.getBlock() instanceof PistonBlock) {
				if (PistonHelper.shouldExtend(world, blockPos, blockState.get(PistonBlock.FACING))) {
					cir.setReturnValue(true);
					cir.cancel();
				}
			}
		}
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return redstoneTorchSignal.get();
	}
}

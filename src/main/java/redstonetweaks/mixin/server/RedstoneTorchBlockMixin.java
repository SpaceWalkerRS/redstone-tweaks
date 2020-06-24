package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.softInversion;
import static redstonetweaks.setting.Settings.redstoneTorchBurnoutDelay;
import static redstonetweaks.setting.Settings.redstoneTorchBurnoutTimerDelay;
import static redstonetweaks.setting.Settings.redstoneTorchDelay;
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
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstonetweaks.helper.PistonHelper;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin {
	
	@Shadow @Final private static BooleanProperty LIT;
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 2))
	private int getRedstoneTorchDelay(int oldDelay) {
		return redstoneTorchDelay.get();
	}
	
	@ModifyConstant(method = "update", constant = @Constant(longValue = 60L))
	private static long updateBurnoutTimerDelay(long oldDelay) {
		return redstoneTorchBurnoutTimerDelay.get();
	}
	
	@ModifyConstant(method = "update", constant = @Constant(intValue = 160))
	private static int updateBurnoutDelay(int oldDelay) {
		return redstoneTorchBurnoutDelay.get();
	}
	
	@Inject(method = "shouldUnpower", at = @At(value = "HEAD"), cancellable = true)
	private void shouldUnpower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		BlockPos blockPos = pos.down();
		
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

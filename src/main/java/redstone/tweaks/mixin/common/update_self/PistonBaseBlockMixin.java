package redstone.tweaks.mixin.common.update_self;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Inject(
		method = "triggerEvent",
		at = @At(
			value = "RETURN",
			ordinal = 1
		)
	)
	private void rtScheduleUpdate1(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (Tweaks.Piston.updateSelf(isSticky())) {
			level.scheduleTick(pos, block(), 1);
		}
	}

	@Inject(
		method = "triggerEvent",
		at = @At(
			value = "RETURN",
			ordinal = 2
		)
	)
	private void rtScheduleUpdate2(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (Tweaks.Piston.updateSelf(isSticky())) {
			level.scheduleTick(pos, block(), 1);
		}
	}
}

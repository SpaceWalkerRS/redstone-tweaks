package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin implements BlockOverrides {

	@Inject(
		method = "neighborChanged",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtNeighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		boolean override = overrideNeighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

		if (override) {
			ci.cancel();
		}
	}

	@Inject(
		method = "triggerEvent",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		Boolean override = overrideTriggerEvent(state, level, pos, type, data);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}
}

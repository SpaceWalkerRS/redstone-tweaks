package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(LeverBlock.class)
public class LeverBlockMixin implements BlockOverrides {

	private boolean ticking;

	@Inject(
		method = "use",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
		if (!ticking && !level.isClientSide()) {
			if (level.getBlockTicks().hasScheduledTick(pos, block())) {
				cir.setReturnValue(InteractionResult.FAIL);
			} else {
				boolean powered = state.getValue(LeverBlock.POWERED);

				int delay = powered ? Tweaks.Lever.delayRisingEdge() : Tweaks.Lever.delayFallingEdge();
				TickPriority priority = powered ? Tweaks.Lever.tickPriorityRisingEdge() : Tweaks.Lever.tickPriorityFallingEdge();

				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);

				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = 15
		)
	)
	private int rtTweakSignal(int signal) {
		return Tweaks.Lever.signal();
	}

	@ModifyConstant(
		method = "getDirectSignal",
		constant = @Constant(
			intValue = 15
		)
	)
	private int rtTweakDirectSignal(int signal) {
		return Tweaks.Lever.signalDirect();
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.use(level, null, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), state.getValue(LeverBlock.FACING), pos, true));
		ticking = false;

		return true;
	}
}

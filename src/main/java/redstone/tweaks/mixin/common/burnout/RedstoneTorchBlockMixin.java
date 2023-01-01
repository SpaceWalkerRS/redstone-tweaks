package redstone.tweaks.mixin.common.burnout;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin {

	@ModifyConstant(
		method = "tick",
		constant = @Constant(
			longValue = 60L
		)
	)
	private long rtTweakBurnoutTimer(long timer) {
		return Tweaks.RedstoneTorch.burnoutTimer();
	}

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakBurnoutDelayAndTickPriority(ServerLevel _level, BlockPos _pos, Block block, int delay, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		delay = Tweaks.RedstoneTorch.delayBurnout();
		TickPriority priority = Tweaks.RedstoneTorch.tickPriorityBurnout();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@ModifyConstant(
		method = "isToggledTooFrequently",
		constant = @Constant(
			intValue = 8
		)
	)
	private static int rtTweakBurnoutCount(int oldValue) {
		return Tweaks.RedstoneTorch.burnoutCount();
	}
}

package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin {

	@Shadow @Final @Mutable private int ticksToStayPressed;
	@Shadow private boolean arrowsCanPress;

	private boolean ticking;

	@Shadow private void press(BlockState state, Level level, BlockPos pos) { }
	@Shadow private void playSound(Player source, LevelAccessor level, BlockPos pos, boolean press) { }

	@Inject(
		method = "press",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		if (ticking) {
			ticksToStayPressed = Tweaks.Button.delayFallingEdge(arrowsCanPress);
		} else {
			int delay = Tweaks.Button.delayRisingEdge(arrowsCanPress);
			TickPriority priority = Tweaks.Button.tickPriorityRisingEdge(arrowsCanPress);

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);

			ci.cancel();
		}
	}

	@Redirect(
		method = "press",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakFallingEdgeTickPriority1(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.Button.tickPriorityFallingEdge(arrowsCanPress));
	}

	@Inject(
		method = "tick", 
		cancellable = true, 
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakRisingEdgeDelay(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (!state.getValue(ButtonBlock.POWERED)) {
			press(state, level, pos);
			playSound(null, level, pos, true);

			ci.cancel();
		}
	}

	@Redirect(
		method = "checkPressed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakFallingEdgeTickPriority2(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.Button.tickPriorityFallingEdge(arrowsCanPress));
	}
}

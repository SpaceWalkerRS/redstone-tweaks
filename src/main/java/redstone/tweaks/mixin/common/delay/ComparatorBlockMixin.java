package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BehaviorOverrides;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;
import redstone.tweaks.interfaces.mixin.ILevel;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin implements DiodeOverrides {

	@Inject(
		method = "getDelay",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelay(BlockState state, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(Tweaks.Comparator.delay());
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/ticks/LevelTickAccess;willTickThisTick(Lnet/minecraft/core/BlockPos;Ljava/lang/Object;)Z"
		)
	)
	private <T> boolean rtTweakMicroTickMode(LevelTickAccess<T> ticks, BlockPos _pos, T block, Level level, BlockPos pos, BlockState state) {
		return microtickMode() ? ((ILevel)level).hasBlockEvent(pos, (Block) block) : ticks.willTickThisTick(pos, block);
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakTickPriorityPrioritized() {
		return Tweaks.Comparator.tickPriorityPrioritized();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;NORMAL:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakTickPriority() {
		return Tweaks.Comparator.tickPriority();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;ILnet/minecraft/world/ticks/TickPriority;)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, TickPriority priority, Level level, BlockPos pos, BlockState state) {
		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);

		delay = BehaviorOverrides.overrideDelay(belowState, Tweaks.Comparator.delay());
		priority = BehaviorOverrides.overrideTickPriority(belowState, priority);

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, () -> BehaviorOverrides.overrideMicrotickMode(belowState, microtickMode()));
	}

	@Inject(
		method = "triggerEvent",
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakMircotickMode(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		overrideTriggerEvent(state, level, pos, type, data);
	}

	@Override
	public boolean microtickMode() {
		return Tweaks.Comparator.microtickMode();
	}
}

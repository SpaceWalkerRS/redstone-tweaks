package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {

	@Redirect(
		method = "setPage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/LecternBlock;signalPageChange(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
	private  void rtTweakRisingEdgeDelayAndTickPriority(Level level, BlockPos pos, BlockState state) {
		if (!level.getBlockTicks().hasScheduledTick(pos, state.getBlock())) {
			int delay = Tweaks.Lectern.delayRisingEdge();
			TickPriority priority = Tweaks.Lectern.tickPriorityRisingEdge();

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
		}
	}
}

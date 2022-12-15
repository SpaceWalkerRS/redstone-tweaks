package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(FrostedIceBlock.class)
public class FrostedIceBlockMixin {

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(ServerLevel _level, BlockPos _pos, Block block, int delay, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		int min = Tweaks.FrostedIce.delayMin();
		int max = Tweaks.FrostedIce.delayMax();
		delay = min + rand.nextInt(max - min);
		TickPriority priority = Tweaks.FrostedIce.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}
}

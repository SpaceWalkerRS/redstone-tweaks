package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

	@Redirect(
		method = "onPlace",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		scheduleOrDoTick(level, pos, state);
	}

	@Redirect(
		method = "addItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private static void rtTweakDelayAndTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int delay, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
		scheduleOrDoTick(level, pos, state);
	}

	private static void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state) {
		int delay = Tweaks.Composter.delay();
		TickPriority priority = Tweaks.Composter.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}
}

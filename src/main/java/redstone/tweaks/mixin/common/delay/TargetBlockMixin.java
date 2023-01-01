package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(TargetBlock.class)
public class TargetBlockMixin {

	@ModifyConstant(
		method = "updateRedstoneOutput",
		constant = @Constant(
			intValue = 8
		)
	)
	private static int rtTweakDelay(int delay) {
		return Tweaks.Target.delay();
	}

	@ModifyConstant(
		method = "updateRedstoneOutput",
		constant = @Constant(
			intValue = 20
		)
	)
	private static int rtTweakArrowDelay(int delay) {
		return Tweaks.Target.delayArrow();
	}

	@Redirect(
		method = "setOutputPower",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private static void rtTweakTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int _delay, LevelAccessor level, BlockState state, int power, BlockPos pos, int delay) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.Target.tickPriority());
	}
}

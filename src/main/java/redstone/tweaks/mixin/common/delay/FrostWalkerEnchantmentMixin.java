package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {

	@Redirect(
		method = "onEntityMoved",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private static void rtTweakDelayAndTickPriority(Level _level, BlockPos pos, Block block, int delay, LivingEntity entity, Level level, BlockPos entityPos, int frostWalkerLevel) {
		BlockState state = level.getBlockState(pos);

		int min = Tweaks.FrostedIce.delayMin();
		int max = Tweaks.FrostedIce.delayMax();
		delay = min + entity.getRandom().nextInt(max - min);
		TickPriority priority = Tweaks.FrostedIce.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}
}

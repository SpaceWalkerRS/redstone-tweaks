package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.entity.HopperBlockEntity;

import redstone.tweaks.Tweaks;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

	@ModifyConstant(
		method = "tryMoveItems",
		constant = @Constant(
			intValue = 8
		)
	)
	private static int rtTweakCooldown1(int cooldown) {
		return Tweaks.Hopper.cooldown();
	}

	@ModifyConstant(
		method = "tryMoveInItem",
		constant = @Constant(
			intValue = 1
		)
	)
	private static int rtTweakPrioritizedCooldown(int difference) {
		return Tweaks.Hopper.cooldown() - Tweaks.Hopper.cooldownPrioritized();
	}

	@ModifyConstant(
		method = "tryMoveInItem",
		constant = @Constant(
			intValue = 8
		)
	)
	private static int rtTweakCooldown2(int cooldown) {
		return Tweaks.Hopper.cooldown();
	}
}

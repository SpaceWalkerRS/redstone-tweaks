package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.entity.item.PrimedTnt;

import redstone.tweaks.Tweaks;;

@Mixin(PrimedTnt.class)
public class PrimedTntMixin {

	@ModifyConstant(
		method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V",
		constant = @Constant(
			intValue = 80
		)
	)
	private int rtTweakFuseTime1(int fuseTime) {
		return Tweaks.TNT.fuseTime();
	}

	@ModifyConstant(
		method = "defineSynchedData",
		constant = @Constant(
			intValue = 80
		)
	)
	private int rtTweakFuseTime2(int oldFuseTime) {
		return Tweaks.TNT.fuseTime();
	}
}

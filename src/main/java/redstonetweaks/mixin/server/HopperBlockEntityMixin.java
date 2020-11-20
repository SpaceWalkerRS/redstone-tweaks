package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.entity.HopperBlockEntity;
import redstonetweaks.setting.Tweaks;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	
	@ModifyVariable(method = "setCooldown", argsOnly = true, at = @At(value = "HEAD"))
	private int modifyTypeValue(int cooldown) {
		if (cooldown == 7) {
			return Tweaks.Hopper.COOLDOWN_PRIORITY.get();
		}
		if (cooldown == 8) {
			return Tweaks.Hopper.COOLDOWN_DEFAULT.get();
		}
		return cooldown;
	}
}

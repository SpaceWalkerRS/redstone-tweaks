package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.entity.HopperBlockEntity;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	
	@ModifyVariable(method = "setCooldown", argsOnly = true, at = @At(value = "HEAD"))
	private int modifyTypeValue(int oldCooldown) {
		if (oldCooldown == 7) {
			return HOPPER.get(PRIORITY_COOLDOWN);
		}
		if (oldCooldown == 8) {
			return HOPPER.get(COOLDOWN);
		}
		return oldCooldown;
	}
}

package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.screen.ScreenHandler;

import redstonetweaks.setting.Tweaks;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
	
	@ModifyConstant(method = "calculateComparatorOutput(Lnet/minecraft/inventory/Inventory;)I", constant = @Constant(floatValue = 14.0F))
	private static float onCalculateComparatorOutputModify14(float oldValue) {
		return Math.max(0, Tweaks.Global.POWER_MAX.get() - 1);
	}
}

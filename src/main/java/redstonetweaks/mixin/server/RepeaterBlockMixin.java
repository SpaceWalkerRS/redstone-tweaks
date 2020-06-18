package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.RepeaterBlock;

import redstonetweaks.setting.Settings;

@Mixin(RepeaterBlock.class)
public class RepeaterBlockMixin {
	
	@ModifyConstant(method = "getUpdateDelayInternal", constant = @Constant(intValue = 2))
	private int getRepeaterDelayMultiplier(int oldDelayMultiplier) {
		return (int)Settings.repeaterDelay.get();
	}
}

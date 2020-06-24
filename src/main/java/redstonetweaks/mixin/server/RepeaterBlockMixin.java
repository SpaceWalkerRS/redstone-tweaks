package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.repeaterDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.RepeaterBlock;;

@Mixin(RepeaterBlock.class)
public class RepeaterBlockMixin {
	
	@ModifyConstant(method = "getUpdateDelayInternal", constant = @Constant(intValue = 2))
	private int getRepeaterDelay(int oldValue) {
		return repeaterDelay.get();
	}
}

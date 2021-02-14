package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.DragonEggBlock;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(DragonEggBlock.class)
public class DragonEggBlockMixin {
	
	@ModifyConstant(method = "getFallDelay", constant = @Constant(intValue = 5))
	private int onGetFallDelayModify5(int oldValue) {
		return Tweaks.DragonEgg.DELAY.get();
	}
}

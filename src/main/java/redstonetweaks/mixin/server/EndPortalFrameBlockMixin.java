package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.EndPortalFrameBlock;

import redstonetweaks.setting.Settings;

@Mixin(EndPortalFrameBlock.class)
public class EndPortalFrameBlockMixin {
	
	@ModifyConstant(method = "getComparatorOutput", constant = @Constant(intValue = 15))
	private int onGetComparatorOutputModify15(int oldValue) {
		return Settings.Global.POWER_MAX.get();
	}
}
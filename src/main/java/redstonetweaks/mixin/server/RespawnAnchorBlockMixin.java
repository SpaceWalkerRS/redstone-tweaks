package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.RespawnAnchorBlock;

import redstonetweaks.setting.Tweaks;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
	
	@ModifyConstant(method = "getComparatorOutput", constant = @Constant(intValue = 15))
	private int onGetComparatorOutputModify15(int oldValue) {
		return Tweaks.Global.POWER_MAX.get();
	}
}

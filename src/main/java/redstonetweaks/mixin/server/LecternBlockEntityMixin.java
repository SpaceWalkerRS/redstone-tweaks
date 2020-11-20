package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.entity.LecternBlockEntity;
import redstonetweaks.setting.Tweaks;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {
	
	@ModifyConstant(method = "getComparatorOutput", constant = @Constant(floatValue = 14.0F))
	private float onGetComparatorOutputModify14(float oldValue) {
		return Math.max(0, Tweaks.Global.POWER_MAX.get() - 1);
	}
}

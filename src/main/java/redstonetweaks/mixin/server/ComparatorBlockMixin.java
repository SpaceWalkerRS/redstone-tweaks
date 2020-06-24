package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.comparatorDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.ComparatorBlock;;

@Mixin(ComparatorBlock.class)
public class ComparatorBlockMixin {
	
	@ModifyConstant(method = "updatePowered", constant = @Constant(intValue = 2))
	private int getComparatorDelay(int oldDelay) {
		return comparatorDelay.get();
	}
}

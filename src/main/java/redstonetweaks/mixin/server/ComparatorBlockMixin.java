package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.ComparatorBlock;

import redstonetweaks.setting.Settings;

@Mixin(ComparatorBlock.class)
public class ComparatorBlockMixin {
	
	@ModifyConstant(method = "updatePowered", constant = @Constant(intValue = 2))
	private int UpdatePoweredDelay(int oldDelay) {
		return (int)Settings.comparatorDelay.get();
	}
}

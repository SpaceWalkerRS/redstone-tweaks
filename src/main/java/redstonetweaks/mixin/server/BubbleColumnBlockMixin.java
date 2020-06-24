package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.bubbleColumnDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BubbleColumnBlock;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 5))
	private int getTickRateWeightedPressurePlateDelay(int oldDelay) {
		return bubbleColumnDelay.get();
	}
}

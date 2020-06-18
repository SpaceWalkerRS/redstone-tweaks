package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BubbleColumnBlock;

import redstonetweaks.setting.Settings;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 5))
	private int getBubbleColumnDelay(int oldDelay) {
		return (int)Settings.bubbleColumnDelay.get();
	}
}

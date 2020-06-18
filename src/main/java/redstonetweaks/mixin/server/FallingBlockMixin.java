package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.FallingBlock;

import redstonetweaks.setting.Settings;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {
	
	// This is the delay gravity blocks have before they start falling.
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 2))
	private int getGravityBlockDelay(int oldDelay) {
		return (int)Settings.gravityBlockDelay.get();
	}
}

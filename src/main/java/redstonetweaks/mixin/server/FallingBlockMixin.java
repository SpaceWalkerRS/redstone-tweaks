package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.gravityBlockDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.FallingBlock;;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {
	
	// Modify the delay gravity blocks have before falling
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 2))
	private int getGravityBlockDelay(int oldDelay) {
		return gravityBlockDelay.get();
	}
}

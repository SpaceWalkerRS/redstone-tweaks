package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.piston.PistonHandler;

import redstonetweaks.setting.Settings;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {
	
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
	private int pushLimit(int oldPushLimit) {
		return (int)Settings.pushLimit.get();
	}
}

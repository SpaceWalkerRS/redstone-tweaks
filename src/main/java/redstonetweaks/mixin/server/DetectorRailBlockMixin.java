package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.DetectorRailBlock;

import redstonetweaks.setting.Settings;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 20))
	private int getDetectorRailDelay(int oldDelay) {
		return (int)Settings.detectorRailDelay.get();
	}
}

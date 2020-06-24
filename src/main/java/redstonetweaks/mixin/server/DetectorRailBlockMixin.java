package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.detectorRailSignal;
import static redstonetweaks.setting.Settings.detectorRailDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.DetectorRailBlock;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 20))
	private int getDetectorRailDelay(int oldDelay) {
		return detectorRailDelay.get();
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return detectorRailSignal.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return detectorRailSignal.get();
	}
}

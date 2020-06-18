package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.ScaffoldingBlock;

import redstonetweaks.setting.Settings;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {
	
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 1))
	private int onBlockAddedDelay(int oldDelay) {
		return (int)Settings.scaffoldingDelay.get();
	}
	
	@ModifyConstant(method = "getStateForNeighborUpdate", constant = @Constant(intValue = 1))
	private int getStateForNeighborUpdateDelay(int oldDelay) {
		return (int)Settings.scaffoldingDelay.get();
	}
}

package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.scaffoldingDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.ScaffoldingBlock;;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {
	
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 1))
	private int onBlockAddedGetDelay(int oldValue) {
		return scaffoldingDelay.get();
	}
	
	@ModifyConstant(method = "getStateForNeighborUpdate", constant = @Constant(intValue = 1))
	private int getStateForNeighborUpdateGetDelay(int oldValue) {
		return scaffoldingDelay.get();
	}
}

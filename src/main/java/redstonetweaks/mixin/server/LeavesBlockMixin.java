package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.LeavesBlock;

import redstonetweaks.setting.Settings;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
	
	@ModifyConstant(method = "getStateForNeighborUpdate", constant = @Constant(intValue = 1, ordinal = 2))
	private int getLeavesDelay(int oldDelayMultiplier) {
		return (int)Settings.leavesDelay.get();
	}
}

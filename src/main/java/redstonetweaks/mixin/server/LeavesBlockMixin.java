package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.leavesDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.LeavesBlock;;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
	
	@ModifyConstant(method = "getStateForNeighborUpdate", constant = @Constant(intValue = 1, ordinal = 2))
	private int getLeavesDelay(int oldValue) {
		return leavesDelay.get();
	}
}

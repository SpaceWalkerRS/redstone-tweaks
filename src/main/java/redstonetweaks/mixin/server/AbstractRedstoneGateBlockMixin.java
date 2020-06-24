package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.repeaterSignal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.AbstractRedstoneGateBlock;

@Mixin(AbstractRedstoneGateBlock.class)
public class AbstractRedstoneGateBlockMixin {
	
	@ModifyConstant(method = "getOutputLevel", constant = @Constant(intValue = 15))
	private int getRepeaterSignal(int oldValue) {
		return repeaterSignal.get();
	}
}

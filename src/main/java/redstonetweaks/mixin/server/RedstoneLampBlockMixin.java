package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.RedstoneLampBlock;

import redstonetweaks.setting.Settings;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin {
	
	@ModifyConstant(method = "neighborUpdate", constant = @Constant(intValue = 4))
	private int neighborUpdateRedstoneLampDelay(int oldScheduledTickDelay) {
		return (int)Settings.redstoneLampDelay.get();
	}
}

package redstone.tweaks.mixin.common.scheduled_ticks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.ticks.LevelTicks;

import redstone.tweaks.Tweaks;

@Mixin(LevelTicks.class)
public class LevelTicksMixin {

	@ModifyVariable(
		method = "tick",
		argsOnly = true,
		at = @At(
			value = "HEAD"
		)
	)
	private int rtTweakScheduledTickLimit(int limit) {
		return Tweaks.Global.scheduledTickLimit();
	}
}

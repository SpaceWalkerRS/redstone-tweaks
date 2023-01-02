package redstone.tweaks.mixin.common.input_bug;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;

import redstone.tweaks.Tweaks;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@Shadow private void tickTime() { }

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;tickTime()V"
		)
	)
	private void rtFixMC172213(ServerLevel level) {
		if (!Tweaks.BugFixes.MC172213()) {
			tickTime();
		}
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtFixMC172213(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if (Tweaks.BugFixes.MC172213()) {
			tickTime();
		}
	}
}

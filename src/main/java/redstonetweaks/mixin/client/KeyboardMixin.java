package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

import redstonetweaks.interfaces.RTIMinecraftClient;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	
	@Shadow @Final protected MinecraftClient client;
	
	@Inject(method = "onKey", cancellable = true, at = @At(value = "FIELD", ordinal = 0, shift = Shift.BEFORE, target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J"))
	private void onOnKeyInjectAfterOnKeyPressed(long window, int key, int scancode, int event, int j, CallbackInfo ci) {
		if (((RTIMinecraftClient)client).getHotkeysManager().onKey(key, scancode, event)) {
			ci.cancel();
		}
	}
}

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

import redstonetweaks.gui.RTMenuScreen;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	
	@Shadow @Final protected MinecraftClient client;
	
	@Inject(method = "onKey", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/options/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V"))
	private void onOnKeyInjectAfterOnKeyPressed(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
		if (key == 82) {
			client.openScreen(new RTMenuScreen(client));
		}
	}
}

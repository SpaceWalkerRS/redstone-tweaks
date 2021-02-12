package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
	
	protected GameMenuScreenMixin(Text title) {
		super(title);
	}
	
	private ButtonWidget toTitleScreenButton;
	
	@Inject(method = "initWidgets", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
	private void onInitWidgetsInjectAtIsInSingleplayer1(CallbackInfo ci, ButtonWidget button) {
		if (client.isInSingleplayer()) {
			toTitleScreenButton = button;
		}
	}
	
	@Inject(method = "tick", at = @At(value = "RETURN"))
	private void onTickInjectAtReturn(CallbackInfo ci) {
		if (toTitleScreenButton != null) {
			toTitleScreenButton.active = client.isPaused();
		}
	}
}

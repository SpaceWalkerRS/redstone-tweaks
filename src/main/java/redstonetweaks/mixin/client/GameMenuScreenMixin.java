package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
	
	protected GameMenuScreenMixin(Text title) {
		super(title);
	}
	
	private ButtonWidget toTitleScreenButton;
	
	@Redirect(method = "initWidgets", at = @At(value = "NEW", ordinal = 7, target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget;"))
	private ButtonWidget onInitWidgetRedirectNewButtonWidget7(int x, int y, int width, int height, Text message, ButtonWidget.PressAction pressAction) {
		addButton(new RTButtonWidget(x, y, width, height, () -> new TranslatableText("Redstone Tweaks Menu"), (menuButton) -> {
			client.openScreen(new RTMenuScreen(client));
		}));
		
		ButtonWidget button = new ButtonWidget(x, y + 24, width, height, message, pressAction);
		
		if (client.isInSingleplayer()) {
			toTitleScreenButton = button;
		}
		
		return button;
	}
	
	@Inject(method = "tick", at = @At(value = "RETURN"))
	private void onTickInjectAtReturn(CallbackInfo ci) {
		if (toTitleScreenButton != null) {
			toTitleScreenButton.active = client.isPaused();
		}
	}
}

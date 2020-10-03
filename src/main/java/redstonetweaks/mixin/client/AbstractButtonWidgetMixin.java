package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Mixin(AbstractButtonWidget.class)
public class AbstractButtonWidgetMixin {
	
	// We don't want to enable depth test because
	// then text behind a button can render in front of it
	@Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V"))
	private void onRenderButtonRedirectEnabledDepthTest() {
		
	}
}

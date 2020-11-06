package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "render", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"))
	public void render(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (!client.options.debugEnabled && (Settings.Global.SHOW_PROCESSING_ORDER.get() > 0) || ((RTIMinecraftClient)client).getWorldTickHandler().tickInProgress()) {
			((RTIMinecraftClient)client).getTickInfoLabelRenderer().render(matrixStack);
		}
		
		if (!((RTIMinecraftClient)client).getWorldTickHandler().doWorldTicks()) {
			client.textRenderer.drawWithShadow(matrixStack, "\u23f8", client.getWindow().getScaledWidth() - 6, 2, 0xFFEEEEEE);
		}
	}
}

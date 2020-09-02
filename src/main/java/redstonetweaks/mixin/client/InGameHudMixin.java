package redstonetweaks.mixin.client;

import static redstonetweaks.setting.SettingsManager.*;

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

import redstonetweaks.helper.MinecraftClientHelper;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "render", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"))
	public void render(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (!client.options.debugEnabled && (GLOBAL.get(SHOW_PROCESSING_ORDER) > 0) || ((MinecraftClientHelper)client).getWorldTickHandler().isTickingWorlds()) {
			((MinecraftClientHelper)client).getTickInfoLabelRenderer().render(matrixStack);
		}
	}
}

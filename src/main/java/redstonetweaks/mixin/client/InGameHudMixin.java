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
import redstonetweaks.mixinterfaces.RTIMinecraftClient;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.WorldTickOptions;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "render", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"))
	public void render(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (!client.options.debugEnabled && (Tweaks.Global.WORLD_TICK_OPTIONS.get().getMode() == WorldTickOptions.Mode.STEP_BY_STEP) || ((RTIMinecraftClient)client).getWorldTickHandler().tickInProgress()) {
			((RTIMinecraftClient)client).getTickInfoLabelRenderer().render(matrixStack);
		}
		
		if (!((RTIMinecraftClient)client).getWorldTickHandler().doWorldTicks()) {
			client.textRenderer.drawWithShadow(matrixStack, "\u23f8", client.getWindow().getScaledWidth() - 6, 2, 0xFFEEEEEE);
		}
	}
}

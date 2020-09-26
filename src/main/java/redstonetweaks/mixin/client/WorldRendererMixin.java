package redstonetweaks.mixin.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.settings.Settings;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "render", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/client/render/WorldRenderer;transparencyShader:Lnet/minecraft/client/gl/ShaderEffect;", opcode = Opcodes.GETFIELD, shift = Shift.BEFORE))
	private void onRenderInjectBeforeTransparencyShader(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		if (Settings.Global.SHOW_NEIGHBOR_UPDATES.get()) {
			((MinecraftClientHelper)client).getNeighborUpdateVisualizer().draw(matrices);
		}
	}
}

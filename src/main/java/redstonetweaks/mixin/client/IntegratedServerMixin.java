package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

import redstonetweaks.helper.MinecraftServerHelper;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isPaused()Z"))
	private boolean onTickRedirectIsPaused(MinecraftClient client) {
		return client.isPaused() && !((MinecraftServerHelper)(Object)this).getWorldTickHandler().isTickingWorlds();
	}
}

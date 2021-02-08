package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import redstonetweaks.client.PermissionManager;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	
	@Shadow @Final protected MinecraftClient client;
	@Shadow protected int clientPermissionLevel;
	
	@Inject(method = "setClientPermissionLevel", at = @At(value = "RETURN"))
	private void onSetClientPermissionLevelInjectAtReturn(int newPermissionLevel, CallbackInfo ci) {
		PermissionManager.permissionsChanged();
	}
}

package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.packet.types.ServerInfoPacket;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	
	@Shadow @Final protected MinecraftServer server; 
	
	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"))
	private void onOnPlayerConnectInjectBeforeOnSpawn(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		((RTIMinecraftServer)server).getPacketHandler().sendPacketToPlayer(new ServerInfoPacket(), player);
		
		((RTIMinecraftServer)server).getSettingsManager().onPlayerJoined(player);
		((RTIMinecraftServer)server).getPresetsManager().onPlayerJoined(player);
		
		((RTIMinecraftServer)server).getWorldTickHandler().onPlayerJoined(player);
	}
}

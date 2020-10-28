package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.interfaces.RTICustomPayloadC2SPacket;
import redstonetweaks.packet.PacketHandler;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	
	@Shadow @Final private MinecraftServer server;
	
	@Inject(method = "onCustomPayload", cancellable = true, at = @At(value = "HEAD"))
	private void onCustomPayload(CustomPayloadC2SPacket minecraftPacket, CallbackInfo ci) {
		RTICustomPayloadC2SPacket packet = (RTICustomPayloadC2SPacket)minecraftPacket;
		
		if (PacketHandler.PACKET_IDENTIFIER.equals(packet.getChannel())) {
			PacketByteBuf buffer = packet.getData();
			
			((RTIMinecraftServer)server).getPacketHandler().onPacketReceived(buffer);
			
			ci.cancel();
		}
	}
}

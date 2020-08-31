package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.packet.PacketHandler;
import redstonetweaks.packet.PlayerJoinedServerPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@Shadow private MinecraftClient client;
	
	@Inject(method = "onGameJoin", at = @At(value = "RETURN"))
	private void onOnGameJoin(GameJoinS2CPacket gameJoinPacket, CallbackInfo ci) {
		PlayerJoinedServerPacket packet = new PlayerJoinedServerPacket(client.player);
		((MinecraftClientHelper)client).getPacketHandler().sendPacket(packet);
	}
	
	@Inject(method = "onCustomPayload", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"))
	private void onOnCustomPayloadInjectAfterForceMainThread(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (PacketHandler.PACKET_IDENTIFIER.equals(packet.getChannel())) {
			PacketByteBuf buffer = packet.getData();
			RedstoneTweaksVersion serverVersion = new RedstoneTweaksVersion(buffer.readByte(), buffer.readByte(), buffer.readByte());
			
			if (serverVersion.equals(RedstoneTweaks.MOD_VERSION)) {
				((MinecraftClientHelper)client).getPacketHandler().onPacketReceived(buffer);
			}
			
			ci.cancel();
		}
	}
}

package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import redstonetweaks.packet.types.AbstractRedstoneTweaksPacket;
import redstonetweaks.server.ServerInfo;

public class ClientPacketHandler extends AbstractPacketHandler {
	
	private MinecraftClient client;
	
	public ClientPacketHandler(MinecraftClient client) {
		this.client = client;
	}
	
	@Override
	public CustomPayloadC2SPacket toCustomPayloadPacket(PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(PACKET_IDENTIFIER, buffer);
	}
	
	@Override
	public void sendPacket(AbstractRedstoneTweaksPacket packet) {
		if (ServerInfo.getModVersion().isValid()) {
			client.getNetworkHandler().sendPacket(encodePacket(packet));
		}
	}
	
	public void onPacketReceived(PacketByteBuf buffer) {
		if (canReadPacket(buffer)) {
			try {
				decodePacket(buffer).execute(client);
			} catch (Exception e) {
				
			}
		}
	}
}

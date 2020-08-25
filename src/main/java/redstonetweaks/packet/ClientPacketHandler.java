package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class ClientPacketHandler extends PacketHandler {
	
	private MinecraftClient client;
	
	public ClientPacketHandler(MinecraftClient client) {
		this.client = client;
	}
	
	@Override
	public CustomPayloadC2SPacket toCustomPayloadPacket(PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(PACKET_IDENTIFIER, buffer);
	}

	@Override
	public void sendPacket(RedstoneTweaksPacket packet) {
		client.getNetworkHandler().sendPacket(encodePacket(packet));
	}

	@Override
	public void onPacketReceived(PacketByteBuf buffer) {
		decodePacket(buffer).execute(client);
	}
}

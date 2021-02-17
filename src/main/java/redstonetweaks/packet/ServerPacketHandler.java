package redstonetweaks.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import redstonetweaks.packet.types.AbstractRedstoneTweaksPacket;

public class ServerPacketHandler extends AbstractPacketHandler {
	
	private MinecraftServer server;
	
	public ServerPacketHandler(MinecraftServer server) {
		this.server = server;
	}
	
	@Override
	public Packet<?> toCustomPayloadPacket(PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(PACKET_IDENTIFIER, buffer);
	}
	
	@Override
	public void sendPacket(AbstractRedstoneTweaksPacket packet) {
		server.getPlayerManager().sendToAll(encodePacket(packet));
	}
	
	public void onPacketReceived(PacketByteBuf buffer, ServerPlayerEntity player) {
		if (canReadPacket(buffer)) {
			try {
				decodePacket(buffer).execute(server, player);
			} catch (Exception e) {
				
			}
		}
	}
	
	public void sendPacketToDimension(AbstractRedstoneTweaksPacket packet, RegistryKey<World> dimension) {
		server.getPlayerManager().sendToDimension(encodePacket(packet), dimension);
	}
	
	public void sendPacketToAround(AbstractRedstoneTweaksPacket packet, RegistryKey<World> dimension, BlockPos pos, double distance) {
		server.getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), distance, dimension, encodePacket(packet));
	}

	public void sendPacketToPlayer(AbstractRedstoneTweaksPacket packet, ServerPlayerEntity player) {
		player.networkHandler.sendPacket(encodePacket(packet));
	}
}

package redstonetweaks.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;

public class ServerPacketHandler extends PacketHandler {
	
	private MinecraftServer server;
	
	public ServerPacketHandler(MinecraftServer server) {
		this.server = server;
	}
	
	@Override
	public Packet<?> toCustomPayloadPacket(PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(PACKET_IDENTIFIER, buffer);
	}
	
	@Override
	public void sendPacket(RedstoneTweaksPacket packet) {
		server.getPlayerManager().sendToAll(encodePacket(packet));
		
	}
	
	public void sendPacketToDimension(RedstoneTweaksPacket packet, RegistryKey<World> dimension) {
		server.getPlayerManager().sendToDimension(encodePacket(packet), dimension);
	}
	
	public void sendPacketToAround(RedstoneTweaksPacket packet, RegistryKey<World> dimension, BlockPos pos, double distance) {
		server.getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), distance, dimension, encodePacket(packet));
	}

	public void sendPacketToPlayer(SettingPacket<?> packet, ServerPlayerEntity player) {
		player.networkHandler.sendPacket(encodePacket(packet));
	}
	
	@Override
	public void onPacketReceived(PacketByteBuf buffer) {
		RedstoneTweaksVersion clientVersion = new RedstoneTweaksVersion(buffer.readByte(), buffer.readByte(), buffer.readByte());
		if (RedstoneTweaks.MOD_VERSION.equals(clientVersion)) {
			try {
				decodePacket(buffer).execute(server);
			} catch (InstantiationException e) {
				
			} catch (IllegalAccessException e) {
				
			}
		}
	}
}

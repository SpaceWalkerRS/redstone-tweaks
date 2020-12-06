package redstonetweaks.packet;

import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.interfaces.RTIMinecraftServer;

public class PlayerJoinedServerPacket extends RedstoneTweaksPacket {
	
	private UUID playerUUID;
	
	public PlayerJoinedServerPacket() {
		
	}
	
	public PlayerJoinedServerPacket(PlayerEntity player) {
		this.playerUUID = player.getUuid();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeUuid(playerUUID);
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		playerUUID = buffer.readUuid();
	}

	@Override
	public void execute(MinecraftServer server) {
		((RTIMinecraftServer)server).getSettingsManager().onPlayerJoined(playerUUID);
		
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
		if (player != null) {
			ServerInfoPacket packet = new ServerInfoPacket();
			((RTIMinecraftServer)server).getPacketHandler().sendPacketToPlayer(packet, player);
		}
	}

	@Override
	public void execute(MinecraftClient client) {
		
	}
}

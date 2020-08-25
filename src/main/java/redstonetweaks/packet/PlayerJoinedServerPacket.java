package redstonetweaks.packet;

import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.helper.MinecraftServerHelper;

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
		((MinecraftServerHelper)server).getSettingsManager().onPlayerJoined(playerUUID);
	}

	@Override
	public void execute(MinecraftClient client) {
		
	}

}

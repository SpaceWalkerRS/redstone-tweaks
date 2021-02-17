package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.util.PacketUtils;

public class ServerInfoPacket extends AbstractRedstoneTweaksPacket {
	
	public RedstoneTweaksVersion serverModVersion;
	
	public ServerInfoPacket() {
		this.serverModVersion = ServerInfo.getModVersion();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeRedstoneTweaksVersion(buffer, serverModVersion);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		serverModVersion = PacketUtils.readRedstoneTweaksVersion(buffer);
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		ServerInfo.updateFromPacket(this);
	}
}

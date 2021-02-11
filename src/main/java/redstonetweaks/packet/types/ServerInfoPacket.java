package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.util.PacketUtils;

public class ServerInfoPacket extends RedstoneTweaksPacket {
	
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
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		ServerInfo.updateFromPacket(this);
	}
}

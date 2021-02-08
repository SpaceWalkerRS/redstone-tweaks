package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.util.PacketUtils;

public class ServerInfoPacket extends RedstoneTweaksPacket {
	
	public RedstoneTweaksVersion modVersion;
	
	public ServerInfoPacket() {
		this.modVersion = ServerInfo.getModVersion();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeRedstoneTweaksVersion(buffer, RedstoneTweaks.MOD_VERSION);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		modVersion = PacketUtils.readRedstoneTweaksVersion(buffer);
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		ServerInfo.updateFromPacket(this);
	}
}

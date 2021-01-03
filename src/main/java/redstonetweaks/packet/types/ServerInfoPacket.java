package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.ServerInfo;
import redstonetweaks.mixinterfaces.RTIMinecraftClient;

public class ServerInfoPacket extends RedstoneTweaksPacket {
	
	public RedstoneTweaksVersion modVersion;
	
	public ServerInfoPacket() {
		this.modVersion = ServerInfo.getModVersion();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(RedstoneTweaks.MOD_VERSION.toString());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		modVersion = RedstoneTweaksVersion.parseVersion(buffer.readString(MAX_STRING_LENGTH));
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		ServerInfo.updateFromPacket(this);
		
		((RTIMinecraftClient)client).getSettingsManager().onServerInfoUpdated();
	}
}

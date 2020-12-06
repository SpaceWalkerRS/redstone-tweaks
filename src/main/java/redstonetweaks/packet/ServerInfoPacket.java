package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.ServerInfo;
import redstonetweaks.interfaces.RTIMinecraftClient;

public class ServerInfoPacket extends RedstoneTweaksPacket {
	
	public RedstoneTweaksVersion modVersion;
	
	public ServerInfoPacket() {
		this.modVersion = ServerInfo.getModVersion();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(modVersion.major);
		buffer.writeByte(modVersion.minor);
		buffer.writeByte(modVersion.patch);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		modVersion = RedstoneTweaksVersion.create(buffer.readByte(), buffer.readByte(), buffer.readByte());
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

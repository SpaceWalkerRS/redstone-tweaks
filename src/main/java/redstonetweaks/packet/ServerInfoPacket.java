package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;

public class ServerInfoPacket extends RedstoneTweaksPacket {
	
	public int modMajor;
	public int modMinor;
	public int modPatch;
	
	public ServerInfoPacket() {
		
	}
	
	public ServerInfoPacket(RedstoneTweaksVersion modVersion) {
		this.modMajor = modVersion.major;
		this.modMinor = modVersion.minor;
		this.modPatch = modVersion.patch;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(modMajor);
		buffer.writeByte(modMinor);
		buffer.writeByte(modPatch);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		modMajor = buffer.readByte();
		modMinor = buffer.readByte();
		modPatch = buffer.readByte();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		RedstoneTweaks.SERVER_VERSION = new RedstoneTweaksVersion(modMajor, modMinor, modPatch);
	}
}

package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import redstonetweaks.helper.MinecraftClientHelper;

public class WorldSyncPacket extends RedstoneTweaksPacket {
	
	public String worldName;
	
	public WorldSyncPacket() {
		
	}
	
	public WorldSyncPacket(World world) {
		this.worldName = world == null ? "-" : world.getRegistryKey().getValue().toString();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(worldName);
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		worldName = buffer.readString();
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((MinecraftClientHelper)client).getWorldHandler().onWorldSyncPacketReceived(this);
		((MinecraftClientHelper)client).getTickInfoLabelRenderer().onWorldSyncPacketReceived(this);
	}

}

package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.mixinterfaces.RTIMinecraftClient;

public class WorldTimeSyncPacket extends RedstoneTweaksPacket {
	
	public long worldTime;
	
	public WorldTimeSyncPacket() {
		
	}
	
	public WorldTimeSyncPacket(long worldTime) {
		this.worldTime = worldTime;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeLong(worldTime);
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		worldTime = buffer.readLong();
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getTickInfoLabelRenderer().onWorldTimeSyncPacketReceived(this);
	}

}

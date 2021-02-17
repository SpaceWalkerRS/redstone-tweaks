package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;

public class WorldTimeSyncPacket extends AbstractRedstoneTweaksPacket {
	
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
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getTickInfoLabelRenderer().syncWorldTime(worldTime);
	}

}

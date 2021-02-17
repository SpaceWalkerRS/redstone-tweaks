package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.util.PacketUtils;

public class WorldSyncPacket extends AbstractRedstoneTweaksPacket {
	
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
		worldName = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().syncWorld(worldName);
		((RTIMinecraftClient)client).getTickInfoLabelRenderer().syncWorld(worldName);
	}
}

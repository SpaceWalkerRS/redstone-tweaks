package redstonetweaks.packet.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.mixin.RTIClientWorld;

public class NeighborUpdateSchedulerPacket extends RedstoneTweaksPacket {

	public boolean hasScheduledUpdates;
	
	public NeighborUpdateSchedulerPacket() {
		
	}
	
	public NeighborUpdateSchedulerPacket(boolean hasScheduledUpdates) {
		this.hasScheduledUpdates = hasScheduledUpdates;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(hasScheduledUpdates);	
	}
	
	public void decode(PacketByteBuf buffer) {
		hasScheduledUpdates = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void execute(MinecraftClient client) {
		((RTIClientWorld)client.world).getNeighborUpdateScheduler().onPacketReceived(this);
	}
}

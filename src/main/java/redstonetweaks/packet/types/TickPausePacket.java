package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.client.PermissionManager;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;

public class TickPausePacket extends AbstractRedstoneTweaksPacket {
	
	public boolean pause;
	
	public TickPausePacket() {
		
	}
	
	public TickPausePacket(boolean pause) {
		this.pause = pause;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(pause);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pause = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (PermissionManager.canUseTickCommand(player)) {
			((RTIMinecraftServer)server).getWorldTickHandler().pauseWorldTicking(pause);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}

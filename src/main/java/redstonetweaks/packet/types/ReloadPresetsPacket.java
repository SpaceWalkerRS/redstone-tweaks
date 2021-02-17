package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.client.PermissionManager;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;

public class ReloadPresetsPacket extends AbstractRedstoneTweaksPacket {
	
	public ReloadPresetsPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player) {
		if (PermissionManager.canEditPresets(player)) {
			((RTIMinecraftServer)server).getPresetsManager().reloadPresets();
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}

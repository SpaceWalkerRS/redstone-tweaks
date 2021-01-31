package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;

public class ReloadPresetsPacket extends RedstoneTweaksPacket {
	
	public ReloadPresetsPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void execute(MinecraftServer server) {
		((RTIMinecraftServer)server).getPresetsManager().reloadPresets();
	}
	
	@Override
	public void execute(MinecraftClient client) {
		
	}
}

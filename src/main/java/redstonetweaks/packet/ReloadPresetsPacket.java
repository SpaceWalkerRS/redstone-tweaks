package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.preset.Presets;

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
		((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().reloadPresets();
	}
	
	@Override
	public void execute(MinecraftClient client) {
		Presets.toDefault();
		((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onReloadPresetsPacketReceived();
	}
}

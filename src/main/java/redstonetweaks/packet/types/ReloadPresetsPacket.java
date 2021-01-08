package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.mixinterfaces.RTIMinecraftClient;
import redstonetweaks.mixinterfaces.RTIMinecraftServer;
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
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(this);
		((RTIMinecraftServer)server).getPresetsManager().reloadPresets();
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			Presets.toDefault();
		}
		((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onReloadPresetsPacketReceived();
	}
}

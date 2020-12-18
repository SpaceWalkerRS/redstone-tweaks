package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class RemovePresetPacket extends RedstoneTweaksPacket {
	
	public static final boolean REMOVE = true;
	public static final boolean PUT_BACK = false;
	
	private String name;
	private boolean action;
	
	public RemovePresetPacket() {
		
	}
	
	public RemovePresetPacket(Preset preset, boolean action) {
		name = preset.getName();
		this.action = action;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(name);
		buffer.writeBoolean(action);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		name = buffer.readString(MAX_STRING_LENGTH);
		action = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (action == REMOVE) {
			Preset preset = Presets.fromName(name);
			
			if (preset != null) {
				((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().removePreset(preset);
			}
		}
		if (action == PUT_BACK) {
			Preset preset = Presets.getRemovedPresetFromName(name);
			
			if (preset != null) {
				((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().unremovePreset(preset);
			}
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (action == REMOVE) {
			Preset preset = Presets.fromName(name);
			
			if (preset != null) {
				Presets.remove(preset);
			}
			
			((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onRemovePresetPacketReceived(preset);
		}
		if (action == PUT_BACK) {
			Preset preset = Presets.getRemovedPresetFromName(name);
			
			if (preset != null) {
				Presets.unremove(preset);
			}
			
			((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onRemovePresetPacketReceived(preset);
		}
	}
}

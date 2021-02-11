package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.listeners.ISettingListener;
import redstonetweaks.packet.types.ApplyPresetPacket;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockPackPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetCategoryPacket;
import redstonetweaks.packet.types.ResetPackPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.types.ISetting;

public class ClientSettingsManager implements ISettingListener {
	
	private final MinecraftClient client;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
	}
	
	@Override
	public void categoryLockedChanged(SettingsCategory category) {
		if (!client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockCategoryPacket(category));
		}
	}
	
	@Override
	public void packLockedChanged(SettingsPack pack) {
		if (!client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockPackPacket(pack));
		}
	}
	
	@Override
	public void settingLockedChanged(ISetting setting) {
		if (!client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockSettingPacket(setting));
		}
	}
	
	@Override
	public void settingValueChanged(ISetting setting) {
		if (!client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new SettingPacket(setting));
		}
	}
	
	public void resetCategory(SettingsCategory category) {
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetCategoryPacket(category));
	}
	
	public void resetPack(SettingsPack pack) {
		if (client.isInSingleplayer()) {
			((RTIMinecraftServer)client.getServer()).getSettingsManager().resetPack(pack);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetPackPacket(pack));
		}
	}
	
	public void resetSetting(ISetting setting) {
		if (client.isInSingleplayer()) {
			((RTIMinecraftServer)client.getServer()).getSettingsManager().resetSetting(setting);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetSettingPacket(setting));
		}
	}
	
	public void applyPreset(Preset preset) {
		if (client.isInSingleplayer()) {
			((RTIMinecraftServer)client.getServer()).getSettingsManager().applyPreset(preset);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ApplyPresetPacket(preset));
		}
	}
	
	public void onConnect() {
		if (!client.isInSingleplayer()) {
			Settings.toDefault();
		}
	}
	
	public void onDisconnect() {
		if (!client.isInSingleplayer()) {
			Settings.toDefault();
		}
	}
}

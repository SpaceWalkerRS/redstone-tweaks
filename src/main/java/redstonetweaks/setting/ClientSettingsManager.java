package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockPackPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetSettingsPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.Setting;

public class ClientSettingsManager {
	
	private final MinecraftClient client;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void lockCategory(SettingsCategory category, boolean locked) {
		if (client.isInSingleplayer()) {
			category.setLocked(locked);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockCategoryPacket(category, locked));
		}
	}
	
	public void lockPack(SettingsPack pack, boolean locked) {
		if (client.isInSingleplayer()) {
			pack.setLocked(locked);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockPackPacket(pack, locked));
		}
	}
	
	public void lockSetting(ISetting setting, boolean locked) {
		if (client.isInSingleplayer()) {
			setting.setLocked(locked);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockSettingPacket(setting, locked));
		}
	}
	
	public <T> void changeSetting(Setting<T> setting, T value) {
		if (client.isInSingleplayer()) {
			setting.set(value);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new SettingPacket(setting, value));
		}
	}
	
	public void resetSetting(ISetting setting) {
		if (client.isInSingleplayer()) {
			setting.reset();
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetSettingPacket(setting));
		}
	}
	
	public void resetSettings(SettingsCategory category) {
		if (client.isInSingleplayer()) {
			category.resetAll();
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetSettingsPacket(category));
		}
	}
	
	public void onServerInfoUpdated() {
		if (ServerInfo.getModVersion().equals(RedstoneTweaks.MOD_VERSION)) {
			Settings.enableAll();
		}
	}
	
	public void onDisconnect() {
		Settings.toDefault();
	}
}

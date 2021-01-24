package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.packet.types.LockCategoryPacket;
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
	
	public boolean canChangeSettings() {
		return ServerInfo.getModVersion().isValid() && client.player.hasPermissionLevel(ServerConfig.Settings.EDIT_PERMISSION_LEVEL.get()) && ServerConfig.Settings.EDIT_GAME_MODES.get(client.interactionManager.getCurrentGameMode());
	}
	
	public boolean canLockSettings() {
		return ServerInfo.getModVersion().isValid() && client.player.hasPermissionLevel(ServerConfig.Settings.LOCK_PERMISSION_LEVEL.get()) && ServerConfig.Settings.LOCK_GAME_MODES.get(client.interactionManager.getCurrentGameMode());
	}
	
	public <T> void changeSetting(Setting<T> setting, T value) {
		if (client.isInSingleplayer()) {
			setting.set(value);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new SettingPacket(setting, value));
		}
	}
	
	public void lockSetting(ISetting setting, boolean locked) {
		if (client.isInSingleplayer()) {
			setting.setLocked(locked);
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockSettingPacket(setting, locked));
		}
	}
	
	public void lockCategory(SettingsCategory category, boolean locked) {
		LockCategoryPacket packet = new LockCategoryPacket(category, locked);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void resetSetting(ISetting setting) {
		ResetSettingPacket packet = new ResetSettingPacket(setting);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void resetSettings(SettingsCategory category) {
		ResetSettingsPacket packet = new ResetSettingsPacket(category);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void onLockCategoryPacketReceived() {
		notifyMenuScreenOfSettingChange(null);
	}
	
	public void onSettingsPacketReceived() {
		notifyMenuScreenOfSettingChange(null);
	}
	
	public void onResetSettingPacketReceived(ISetting setting) {
		notifyMenuScreenOfSettingChange(setting);
	}
	
	public void onResetSettingsPacketReceived() {
		notifyMenuScreenOfSettingChange(null);
	}
	
	private void notifyMenuScreenOfSettingChange(ISetting setting) {
		if (client.currentScreen instanceof RTMenuScreen) {
			((RTMenuScreen)client.currentScreen).onSettingChanged(setting);
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

package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.mixinterfaces.RTIMinecraftClient;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetSettingsPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.preset.ClientPresetsManager;
import redstonetweaks.setting.types.ISetting;

public class ClientSettingsManager {
	
	private final MinecraftClient client;
	private final ClientPresetsManager presetsManager;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
		this.presetsManager = new ClientPresetsManager(client);
	}
	
	public ClientPresetsManager getPresetsManager() {
		return presetsManager;
	}
	
	public boolean canChangeSettings() {
		return ServerInfo.getModVersion().isValid() && client.player.hasPermissionLevel(ServerConfig.Settings.EDIT_PERMISSION_LEVEL.get()) && ServerConfig.Settings.EDIT_GAME_MODES.get(client.interactionManager.getCurrentGameMode());
	}
	
	public boolean canLockSettings() {
		return ServerInfo.getModVersion().isValid() && client.player.hasPermissionLevel(ServerConfig.Settings.LOCK_PERMISSION_LEVEL.get()) && ServerConfig.Settings.LOCK_GAME_MODES.get(client.interactionManager.getCurrentGameMode());
	}
	
	public void changeSetting(ISetting setting, String value) {
		SettingPacket packet = new SettingPacket(setting, value);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
	}
	
	public void lockSetting(ISetting setting, boolean locked) {
		LockSettingPacket packet = new LockSettingPacket(setting, locked);
		((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
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
	
	public void onSettingPacketReceived(ISetting setting) {
		notifyMenuScreenOfSettingChange(setting);
	}
	
	public void onLockSettingPacketReceived(ISetting setting) {
		notifyMenuScreenOfSettingChange(setting);
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
		presetsManager.onDisconnect();
	}
}

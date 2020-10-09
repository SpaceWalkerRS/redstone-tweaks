package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.packet.ResetSettingPacket;
import redstonetweaks.packet.ResetSettingsPacket;
import redstonetweaks.packet.SettingPacket;
import redstonetweaks.setting.types.ISetting;

public class ClientSettingsManager {
	
	private final MinecraftClient client;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public boolean canChangeSettings() {
		return client.player.hasPermissionLevel(2);
	}
	
	public void onSettingChanged(ISetting setting) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			SettingPacket packet = new SettingPacket(setting);
			((MinecraftClientHelper)client).getPacketHandler().sendPacket(packet);
		}
		notifyMenuScreenOfSettingChange(setting);
	}
	
	public void onSettingReset(ISetting setting) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			ResetSettingPacket packet = new ResetSettingPacket(setting);
			((MinecraftClientHelper)client).getPacketHandler().sendPacket(packet);
		}
		notifyMenuScreenOfSettingChange(setting);
	}
	
	public void onSettingsReset() {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			ResetSettingsPacket packet = new ResetSettingsPacket();
			((MinecraftClientHelper)client).getPacketHandler().sendPacket(packet);
		}
		notifyMenuScreenOfSettingChange(null);
	}
	
	public void onSettingPacketReceived(ISetting setting) {
		notifyMenuScreenOfSettingChange(setting);
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
	
	public void onDisconnect() {
		resetSettings();
	}
	
	private void resetSettings() {
		for (SettingsPack pack : Settings.SETTINGS_PACKS) {
			pack.getSettings().forEach(setting -> setting.reset());
		}
	}
}

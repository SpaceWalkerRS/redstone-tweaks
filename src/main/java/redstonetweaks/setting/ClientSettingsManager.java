package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.packet.ResetSettingPacket;
import redstonetweaks.packet.ResetSettingsPacket;
import redstonetweaks.packet.SettingPacket;
import redstonetweaks.packet.SettingsPacket;
import redstonetweaks.setting.types.ISetting;

public class ClientSettingsManager {
	
	private final MinecraftClient client;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public boolean canChangeSettings() {
		return ((RTIMinecraftClient)client).getServerInfo().getModVersion().isValid() && client.player.hasPermissionLevel(2);
	}
	
	public boolean canLockSettings() {
		return canChangeSettings();
	}
	
	public void onSettingChanged(ISetting setting) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			SettingPacket packet = new SettingPacket(setting);
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		}
		notifyMenuScreenOfSettingChange(setting);
	}
	
	public void onSettingsChanged(SettingsCategory category) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			SettingsPacket packet = new SettingsPacket(category);
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		}
		notifyMenuScreenOfSettingChange(null);
	}
	
	public void onSettingReset(ISetting setting) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			ResetSettingPacket packet = new ResetSettingPacket(setting);
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
		}
		notifyMenuScreenOfSettingChange(setting);
	}
	
	public void onSettingsReset(SettingsCategory category) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			ResetSettingsPacket packet = new ResetSettingsPacket(category);
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
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
	
	public void onServerInfoUpdated() {
		if (((RTIMinecraftClient)client).getServerInfo().getModVersion().equals(RedstoneTweaks.MOD_VERSION)) {
			Settings.enableAll();
		}
	}
	
	public void onDisconnect() {
		Settings.toDefault();
	}
}

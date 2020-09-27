package redstonetweaks.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import redstonetweaks.gui.RedstoneTweaksMenuScreen;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.packet.SettingPacket;
import redstonetweaks.settings.types.ISetting;

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
	}
	
	public void onSettingPacketReceived(ISetting setting) {
		Screen screen = client.currentScreen;
		if (screen instanceof RedstoneTweaksMenuScreen) {
			((RedstoneTweaksMenuScreen)screen).settingChangedOnServer(setting);
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

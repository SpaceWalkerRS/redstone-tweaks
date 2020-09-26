package redstonetweaks.settings;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.packet.SettingPacket;
import redstonetweaks.settings.types.ISetting;

public class ClientSettingsManager {
	
	private MinecraftClient client;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
	}
	
	public void onSettingChanged(ISetting setting) {
		if (!client.isInSingleplayer() || client.getServer().isRemote()) {
			SettingPacket packet = new SettingPacket(setting);
			((MinecraftClientHelper)client).getPacketHandler().sendPacket(packet);
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

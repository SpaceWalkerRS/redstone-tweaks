package redstonetweaks.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.SettingPacket;
import redstonetweaks.packet.SettingsPacket;
import redstonetweaks.settings.types.ISetting;

public class ServerSettingsManager {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String SETTINGS_PATH = "settings.txt";
	
	private MinecraftServer server;
	
	public ServerSettingsManager(MinecraftServer server) {
		this.server = server;

		onStartUp();
	}
	
	private void onStartUp() {
		loadSettings();
	}
	
	public void onShutdown() {
		saveSettings();
	}
	
	private void loadSettings() {
		File settingsFile = getSettingsFile();

		if (settingsFile.isFile()) {
			try (BufferedReader br = new BufferedReader(new FileReader(settingsFile))) {
				String line;
				
				if ((line = br.readLine()) == null) {
					return;
				}
				if (!RedstoneTweaks.SETTINGS_VERSION.equals(RedstoneTweaksVersion.fromString(line))) {
					return;
				}
				
				while ((line = br.readLine()) != null) {
					String[] args = line.split(" = ", 2);
					
					ISetting setting = Settings.getSettingFromId(args[0]);
					if (setting != null) {
						setting.setFromText(args[1]);
					}
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	private void saveSettings() {
		File settingsFile = getSettingsFile();
		
		try {
			if (!settingsFile.isFile()) {
				settingsFile.createNewFile();
			}
		} catch (IOException e) {
			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(settingsFile))) {
			bw.write(RedstoneTweaks.SETTINGS_VERSION.toString());
			bw.newLine();
			
			for (SettingsPack pack : Settings.SETTINGS_PACKS) {
				for (ISetting setting : pack.getSettings()) {
					bw.write(setting.getId());
					bw.write(" = ");
					bw.write(setting.getAsText());
					bw.newLine();
				}
			}
		} catch (IOException e) {
			
		}
	}
	
	private File getCacheDir() {
		File directory = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), CACHE_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private File getSettingsFile() {
		return new File(getCacheDir(), SETTINGS_PATH);
	}
	
	public void onSettingPacketReceived(ISetting setting) {
		if (server.isDedicated() || server.isRemote()) {
			SettingPacket packet = new SettingPacket(setting);
			((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
		}
	}

	public void onPlayerJoined(UUID playerUUID) {
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
		if (player != null) {
			updateSettingsOfPlayer(player);
		}
	}
	
	private void updateSettingsOfPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((MinecraftServerHelper)server).getPacketHandler();
		SettingsPacket packet = new SettingsPacket(Settings.settingCount);
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
}

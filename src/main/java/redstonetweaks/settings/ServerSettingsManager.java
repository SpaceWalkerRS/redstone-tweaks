package redstonetweaks.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;

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

				SettingsPack currentPack = null;
				while ((line = br.readLine()) != null) {
					if (line.endsWith(":")) {
						currentPack = Settings.getPackFromName(line.substring(0, line.length() - 1));
					} else if (currentPack != null) {
						String[] args = line.split("=", 0);
						
						ISetting setting = currentPack.getSettingFromName(args[0]);
						if (setting != null) {
							setting.setFromText(args[1]);
						}
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
				bw.write(pack.getName() + ":");
				bw.newLine();

				for (ISetting setting : pack.getSettings()) {
					bw.write(setting.getName());
					bw.write("=");
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
}

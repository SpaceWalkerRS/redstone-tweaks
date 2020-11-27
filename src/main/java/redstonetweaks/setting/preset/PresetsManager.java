package redstonetweaks.setting.preset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.util.WorldSavePath;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class PresetsManager {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String PRESETS_PATH = "presets";
	
	private final ServerSettingsManager settingsManager;
	
	public PresetsManager(ServerSettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}
	
	private void loadPreset(String name) {
		File presetsFile = getPresetFile(name);

		if (presetsFile.isFile()) {
			try (BufferedReader br = new BufferedReader(new FileReader(presetsFile))) {
				String line;
				
				if ((line = br.readLine()) == null) {
					return;
				}
				if (!RedstoneTweaks.SETTINGS_VERSION.equals(RedstoneTweaksVersion.parseVersion(line))) {
					return;
				}
				
				while ((line = br.readLine()) != null) {
					try {
						String[] args = line.split(" = ", 2);
						
						ISetting setting = Settings.getSettingFromId(args[0]);
						if (setting != null) {
							setting.setFromString(args[1]);
						}
					} catch (Exception e) {
						
					}
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	private void savePreset(String name) {
		File settingsFile = getPresetFile(name);
		
		try {
			if (!settingsFile.isFile()) {
				settingsFile.createNewFile();
			}
		} catch (IOException e) {
			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(settingsFile))) {
			bw.write(RedstoneTweaks.SETTINGS_VERSION.toString());
			bw.newLine();
			
			for (ISetting setting : Settings.ALL) {
				bw.write(setting.getId());
				bw.write(" = ");
				bw.write(setting.getAsString());
				
				bw.newLine();
			}
		} catch (IOException e) {
			
		}
	}
	
	private File getCacheDir() {
		File directory = new File(settingsManager.getServer().getSavePath(WorldSavePath.ROOT).toFile(), CACHE_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private File getPresetsFolder() {
		File directory = new File(getCacheDir(), PRESETS_PATH);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private File getPresetFile(String name) {
		return new File(getPresetsFolder(), name);
	}
}

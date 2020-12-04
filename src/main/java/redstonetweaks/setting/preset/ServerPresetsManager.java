package redstonetweaks.setting.preset;

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
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class ServerPresetsManager {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String PRESETS_PATH = "presets";
	
	private final MinecraftServer server;
	private final ServerSettingsManager settingsManager;
	
	public ServerPresetsManager(MinecraftServer server, ServerSettingsManager settingsManager) {
		this.server = server;
		this.settingsManager = settingsManager;
		
		onStartUp();
	}
	
	public void onStartUp() {
		loadPresets();
	}
	
	public void onShutdown() {
		savePresets();
	}
	
	private void loadPresets() {
		File directory = getPresetsFolder();
		
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				loadPreset(file);
			}
		}
	}
	
	private void loadPreset(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			
			if ((line = br.readLine()) == null) {
				return;
			}
			if (!RedstoneTweaks.SETTINGS_VERSION.equals(RedstoneTweaksVersion.parseVersion(line))) {
				return;
			}
			
			if ((line = br.readLine()) == null) {
				return;
			}
			Preset preset = Presets.fromNameOrCreate(line);
			
			if ((line = br.readLine()) == null) {
				return;
			}
			preset.setDescription(line);
			
			if ((line = br.readLine()) == null) {
				return;
			}
			try {
				preset.setMode(Preset.Mode.valueOf(line));
			} catch (Exception e) {
				
			}
			
			while ((line = br.readLine()) != null) {
				try {
					String[] args = line.split(" = ", 2);
					
					ISetting setting = Settings.getSettingFromId(args[0]);
					if (setting != null) {
						setting.setPresetValueFromString(preset, args[1]);
					}
				} catch (Exception e) {
					
				}
			}
		} catch (IOException e) {
			
		}
	}
	
	private void savePresets() {
		for (Preset preset : Presets.ALL) {
			if (preset.isEditable()) {
				savePreset(preset);
			}
		}
	}
	
	private void savePreset(Preset preset) {
		File file = getPresetFile(preset);
		
		try {
			if (!file.isFile()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(RedstoneTweaks.SETTINGS_VERSION.toString());
			bw.newLine();
			
			bw.write(preset.getName());
			bw.newLine();
			bw.write(preset.getDescription());
			bw.newLine();
			bw.write(preset.getMode().toString());
			bw.newLine();
			
			for (ISetting setting : Settings.ALL) {
				bw.write(setting.getId());
				bw.write(" = ");
				bw.write(setting.getValueAsString());
				
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
	
	private File getPresetFile(Preset preset) {
		return new File(getPresetsFolder(), preset.getName());
	}
}

package redstonetweaks.setting.preset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.packet.PresetPacket;
import redstonetweaks.packet.PresetsPacket;
import redstonetweaks.packet.RemovePresetPacket;
import redstonetweaks.packet.ApplyPresetPacket;
import redstonetweaks.packet.ServerPacketHandler;
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
			
			String name = getPresetNameFromFile(file);
			Preset preset = Presets.fromName(name);
			if (preset == null) {
				preset = new Preset(name, name, "", Preset.Mode.SET, true);
			}
			
			Presets.tryRegister(preset);
			
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
	
	public void reloadPresets() {
		savePresets();
		loadPresets();
		
		updatePresetsOfPlayer(null);
	}
	
	private void savePresets() {
		File directory = getPresetsFolder();
		
		for (File file : directory.listFiles()) {
			if (!file.isDirectory() && Presets.getRemovedPresetFromSavedName(getPresetNameFromFile(file)) != null) {
				file.delete();
			}
		}
		Presets.cleanUp();
		
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
			
			bw.write(preset.getDescription());
			bw.newLine();
			bw.write(preset.getMode().toString());
			bw.newLine();
			
			for (ISetting setting : Settings.ALL) {
				if (setting.hasPreset(preset)) {
					bw.write(setting.getId());
					bw.write(" = ");
					bw.write(setting.getValueAsString());
					
					bw.newLine();
				}
			}
		} catch (IOException e) {
			
		}
	}
	
	private String getPresetNameFromFile(File file) {
		String name = file.getName();
		
		if (name.length() <= 4) {
			return "";
		}
		
		return name.substring(0, name.length() - 4);
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
		return new File(getPresetsFolder(), preset.getName() + ".txt");
	}
	
	public void removePreset(Preset preset) {
		Presets.remove(preset);
		
		RemovePresetPacket packet = new RemovePresetPacket(preset, RemovePresetPacket.REMOVE);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void unremovePreset(Preset preset) {
		Presets.unremove(preset);
		
		RemovePresetPacket packet = new RemovePresetPacket(preset, RemovePresetPacket.PUT_BACK);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void duplicatePreset(Preset preset) {
		String name;
		int i = 0;
		do {
			i++;
			name = preset.getName() + " (" + i + ")";
		} while (!Presets.isNameValid(name));
		
		PresetEditor editor = new PresetEditor(name, preset.getDescription(), preset.getMode());
		
		for (ISetting setting : Settings.ALL) {
			if (setting.hasPreset(preset)) {
				editor.addSetting(setting);
				editor.copyPresetValue(setting, preset);
			}
		}
		
		if (editor.canSave()) {
			editor.saveChanges();
			
			PresetPacket packet = new PresetPacket(editor);
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void applyPreset(Preset preset) {
		preset.apply();
		
		ApplyPresetPacket packet = new ApplyPresetPacket(preset);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onPresetPacketReceived(PresetEditor editor) {
		PresetPacket packet = new PresetPacket(editor);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onPlayerJoined(ServerPlayerEntity player) {
		updatePresetsOfPlayer(player);
	}
	
	private void updatePresetsOfPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)server).getPacketHandler();
		PresetsPacket packet = new PresetsPacket(Presets.ALL);
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
}

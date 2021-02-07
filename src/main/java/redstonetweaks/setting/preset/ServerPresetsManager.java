package redstonetweaks.setting.preset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.listeners.IPresetListener;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.PresetsPacket;
import redstonetweaks.packet.types.RemovePresetPacket;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class ServerPresetsManager implements IPresetListener {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String PRESETS_PATH = "presets";
	
	private final MinecraftServer server;
	
	private boolean deaf;
	
	public ServerPresetsManager(MinecraftServer server) {
		this.server = server;
	}
	
	@Override
	public void presetChanged(PresetEditor editor) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new PresetPacket(editor));
		}
	}
	
	@Override
	public void presetRemoved(Preset preset) {
		System.out.println(preset.getName() + " removed");
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new RemovePresetPacket(preset));
		}
	}
	
	public void onStartUp() {
		loadPresets();
		
		Presets.addListener(this);
	}
	
	public void onShutdown() {
		Presets.removeListener(this);
		
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
			
			String name = getPresetNameFromFile(file);
			Preset preset = Presets.fromName(name);
			if (preset == null) {
				preset = new Preset(name, name, "", Preset.Mode.SET, true);
			}
			
			PresetEditor editor = Presets.editPreset(preset);
			
			if ((line = br.readLine()) == null) {
				return;
			}
			editor.setDescription(line);
			
			if ((line = br.readLine()) == null) {
				return;
			}
			try {
				editor.setMode(Preset.Mode.valueOf(line));
			} catch (Exception e) {
				
			}
			
			while ((line = br.readLine()) != null) {
				try {
					String[] args = line.split(" = ", 2);
					
					ISetting setting = Settings.getSettingFromId(args[0]);
					if (setting != null) {
						editor.addSetting(setting);
						editor.setValueFromString(setting, args[1]);
					}
				} catch (Exception e) {
					
				}
			}
			
			editor.trySaveChanges();
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
		
		for (Preset preset : Presets.getAllPresets()) {
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
			bw.write(preset.getDescription());
			bw.newLine();
			bw.write(preset.getMode().toString());
			bw.newLine();
			
			for (ISetting setting : Settings.getSettings()) {
				if (setting.hasPreset(preset)) {
					bw.write(String.format("%s = %s", setting.getId(), setting.getPresetValueAsString(preset)));
					
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
		File directory = new File(server.getRunDirectory(), CACHE_DIRECTORY);

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
		return new File(getPresetsFolder(), String.format("%s.txt", preset.getName()));
	}
	
	public void onPlayerJoined(ServerPlayerEntity player) {
		updatePresetsOfPlayer(player);
	}
	
	private void updatePresetsOfPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)server).getPacketHandler();
		PresetsPacket packet = new PresetsPacket(Presets.getAllPresets());
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
}

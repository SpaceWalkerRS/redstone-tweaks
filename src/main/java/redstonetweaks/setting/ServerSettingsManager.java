package redstonetweaks.setting;

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
import net.minecraft.world.TickPriority;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.SettingPacket;

public class ServerSettingsManager extends SettingsManager {
	
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
				RedstoneTweaksVersion settingsVersion = RedstoneTweaksVersion.fromString(line);
				if (!RedstoneTweaks.VERSION.equals(settingsVersion)) {
					return;
				}
				
				SettingsPack currentPack = null;
				while ((line = br.readLine()) != null) {
					if (line.endsWith(":")) {
						currentPack = SETTINGS_PACKS.get(line.substring(0, line.length() - 1));
					} else {
						String[] args = line.split("=", 0);
						Setting<?> setting = SETTINGS.get(args[0]);
						int value = Integer.parseInt(args[1]);
						loadSettingFromInt(currentPack, setting, value);
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
			bw.write(RedstoneTweaks.VERSION.toString());
			bw.newLine();
			
			for (SettingsPack pack : SETTINGS_PACKS.values()) {
				bw.write(pack.getName() + ":");
				bw.newLine();
				
				for (Setting<?> setting : pack.getSettings()) {
					bw.write(setting.getName());
					bw.write("=");
					bw.write(Integer.toString(settingToInt(pack, setting)));
					bw.newLine();
				}
			}
		} catch (IOException e) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadSettingFromInt(SettingsPack pack, Setting<?> setting, int value) {
		Property<?> property = pack.getProperty(setting);
		if (property instanceof BooleanProperty) {
			pack.set((Setting<BooleanProperty>)setting, value == 1);
		} else if (property instanceof IntegerProperty) {
			pack.set((Setting<IntegerProperty>)setting, value);
		} else if (property instanceof TickPriorityProperty) {
			pack.set((Setting<TickPriorityProperty>)setting, TickPriority.byIndex(value));
		} else {
			throw new IllegalStateException("unknown setting type");
		}
	}
	
	@SuppressWarnings("unchecked")
	private Integer settingToInt(SettingsPack pack, Setting<?> setting) {
		Property<?> property = pack.getProperty(setting);
		if (property instanceof BooleanProperty) {
			return pack.get((Setting<BooleanProperty>)setting) ? 1 : 0;
		} else if (property instanceof IntegerProperty) {
			return pack.get((Setting<IntegerProperty>)setting);
		} else if (property instanceof TickPriorityProperty) {
			return pack.get((Setting<TickPriorityProperty>)setting).getIndex();
		}
		
		throw new IllegalStateException("unknown setting type");
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
	
	@Override
	public <T> void updateSetting(SettingsPack pack, Setting<? extends Property<T>> setting, T value) {
		pack.set(setting, value);
		
		SettingPacket<T> packet = new SettingPacket<>(pack, setting);
		((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onPlayerJoined(UUID UUID) {
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID);
		if (player != null) {
			updateSettingsOfPlayers(player);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> void updateSettingsOfPlayers(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((MinecraftServerHelper)server).getPacketHandler();
		
		for (SettingsPack pack : SETTINGS_PACKS.values()) {
			for (Setting<?> setting : pack.getSettings()) {
				if (!pack.isDefault((Setting<? extends Property<T>>)setting)) {
					SettingPacket<T> packet = new SettingPacket<>(pack, (Setting<? extends Property<T>>)setting);
					
					if (player == null) {
						packetHandler.sendPacket(packet);
					} else {
						packetHandler.sendPacketToPlayer(packet, player);
					}
				}
			}
		}
	}
}

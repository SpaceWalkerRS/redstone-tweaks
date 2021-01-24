package redstonetweaks.setting;

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
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetSettingsPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.packet.types.SettingsPacket;
import redstonetweaks.setting.types.ISetting;

public class ServerSettingsManager implements ISettingChangeListener {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String SETTINGS_PATH = "settings.txt";
	
	private final MinecraftServer server;
	
	private boolean deaf;
	
	public ServerSettingsManager(MinecraftServer server) {
		this.server = server;
	}
	
	public MinecraftServer getServer() {
		return server;
	}
	
	
	public void onStartUp() {
		Settings.toDefault();
		Settings.enableAll();
		
		loadSettings();
		
		Settings.addChangeListener(this);
	}
	
	public void onShutdown() {
		Settings.removeChangeListener(this);
		
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
			
			for (ISetting setting : Settings.ALL.values()) {
				bw.write(setting.getId());
				bw.write(" = ");
				bw.write(setting.getAsString());
				
				bw.newLine();
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
	
	// Setting changes occur on a client and are then sent to the server,
	// which then notifies all clients of the change
	public void onLockCategoryPacketReceived(SettingsCategory category) {
		LockCategoryPacket packet = new LockCategoryPacket(category);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onResetSettingPacketReceived(ISetting setting) {
		ResetSettingPacket packet = new ResetSettingPacket(setting);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onResetSettingsPacketReceived() {
		ResetSettingsPacket packet = new ResetSettingsPacket();
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onPlayerJoined(ServerPlayerEntity player) {
		if (server.isRemote()) {
			updateSettingsOfPlayer(player);
		}
	}
	
	private void updateSettingsOfPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)server).getPacketHandler();
		SettingsPacket packet = new SettingsPacket(Settings.ALL.values());
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
	
	@Override
	public void settingLockedChanged(ISetting setting) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new LockSettingPacket(setting));
		}
	}
	
	@Override
	public void settingValueChanged(ISetting setting) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new SettingPacket(setting));
		}
	}
}

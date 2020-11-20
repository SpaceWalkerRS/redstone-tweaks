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
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.packet.ResetSettingPacket;
import redstonetweaks.packet.ResetSettingsPacket;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.SettingPacket;
import redstonetweaks.packet.SettingsPacket;
import redstonetweaks.setting.types.ISetting;

public class ServerSettingsManager {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String SETTINGS_PATH = "settings.txt";
	
	private MinecraftServer server;
	
	public ServerSettingsManager(MinecraftServer server) {
		this.server = server;

		onStartUp();
	}
	
	private void onStartUp() {
		Settings.enableAll();
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
	public void onSettingPacketReceived(ISetting setting) {
		if (server.isDedicated() || server.isRemote()) {
			SettingPacket packet = new SettingPacket(setting);
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void onResetSettingPacketReceived(ISetting setting) {
		if (server.isDedicated() || server.isRemote()) {
			ResetSettingPacket packet = new ResetSettingPacket(setting);
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void onResetSettingsPacketReceived() {
		if (server.isDedicated() || server.isRemote()) {
			ResetSettingsPacket packet = new ResetSettingsPacket();
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
		}
	}
	
	public void onPlayerJoined(UUID playerUUID) {
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
		if (player != null) {
			updateSettingsOfPlayer(player);
		}
	}
	
	private void updateSettingsOfPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)server).getPacketHandler();
		SettingsPacket packet = new SettingsPacket(null);
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
}

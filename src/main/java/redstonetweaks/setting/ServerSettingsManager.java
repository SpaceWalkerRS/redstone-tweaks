package redstonetweaks.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.listeners.ISettingListener;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.ApplyPresetPacket;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockPackPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetCategoryPacket;
import redstonetweaks.packet.types.ResetPackPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.packet.types.SettingsPacket;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class ServerSettingsManager implements ISettingListener {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String SETTINGS_PATH = "settings";
	private static final String FILE_EXTENSION = "rts";
	
	private final MinecraftServer server;
	
	private boolean deaf;
	
	public ServerSettingsManager(MinecraftServer server) {
		this.server = server;
	}
	
	@Override
	public void categoryLockedChanged(SettingsCategory category) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new LockCategoryPacket(category));
		}
	}
	
	@Override
	public void packLockedChanged(SettingsPack pack) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new LockPackPacket(pack));
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
	
	public void resetCategory(SettingsCategory category) {
		deaf = true;
		
		category.resetAll();
		if (server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new ResetCategoryPacket(category));
		}
		
		deaf = false;
	}
	
	public void resetPack(SettingsPack pack) {
		deaf = true;
		
		pack.resetAll();
		if (server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new ResetPackPacket(pack));
		}
		
		deaf = false;
	}
	
	public void resetSetting(ISetting setting) {
		deaf = true;
		
		setting.reset();
		if (server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new ResetSettingPacket(setting));
		}
		
		deaf = false;
	}
	
	public void applyPreset(Preset preset) {
		deaf = true;
		
		preset.apply();
		if (server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new ApplyPresetPacket(preset));
		}
		
		deaf = false;
	}
	
	public MinecraftServer getServer() {
		return server;
	}
	
	public void onStartUp() {
		Settings.toDefault();
		Settings.enableAll();
		
		loadSettings();
		
		Settings.addListener(this);
	}
	
	public void onShutdown() {
		Settings.removeListener(this);
		
		saveSettings();
	}

	public void onPlayerJoined(ServerPlayerEntity player) {
		if (server.isRemote()) {
			sendSettingsToPlayer(player);
		}
	}
	
	private void sendSettingsToPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)server).getPacketHandler();
		SettingsPacket packet = new SettingsPacket(Settings.getSettings());
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
	
	private void loadSettings() {
		RedstoneTweaks.LOGGER.info(String.format("Loading settings for \'%s\'", server.getSaveProperties().getLevelName()));
		
		File directory = getSettingsFolder();
		
		for (File file : directory.listFiles()) {
			if (file.isFile() && file.getName().endsWith(FILE_EXTENSION)) {
				loadSettings(file);
			}
		}
	}
	
	private void loadSettings(File file) {
		try (FileInputStream stream = new FileInputStream(file)) {
			byte[] data = IOUtils.toByteArray(stream);
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(data));
			
			readSettings(buffer);
		} catch (Exception e) {
			
		}
	}
	
	private void readSettings(PacketByteBuf buffer) {
		SettingsCategory category = Settings.getCategoryFromName(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		
		if (category != null) {
			category.setLocked(buffer.readBoolean());
			
			int packsCount = buffer.readInt();
			for (int i = 0; i < packsCount; i++) {
				SettingsPack pack = Settings.getPackFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
				
				if (pack != null) {
					pack.setLocked(buffer.readBoolean());
					
					int settingsCount = buffer.readInt();
					for (int j = 0; j < settingsCount; j++) {
						ISetting setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
						
						if (setting != null) {
							setting.setLocked(buffer.readBoolean());
							setting.decode(buffer);
						}
					}
				}
			}
		}
	}
	
	private void saveSettings() {
		RedstoneTweaks.LOGGER.info(String.format("Saving settings for \'%s\'", server.getSaveProperties().getLevelName()));
		
		for (SettingsCategory category : Settings.getCategories()) {
			saveSettings(category);
		}
	}
	
	private void saveSettings(SettingsCategory category) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		writeSettings(category, buffer);
		
		if (buffer.hasArray()) {
			File file = getSettingsFile(category);
			
			try {
				if (!file.isFile()) {
					file.createNewFile();
				}
				
				try (FileOutputStream stream = new FileOutputStream(file)) {
					stream.write(buffer.array());
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	private void writeSettings(SettingsCategory category, PacketByteBuf buffer) {
		buffer.writeString(category.getName());
		buffer.writeBoolean(category.isLocked());
		
		buffer.writeInt(category.getPacks().size());
		for (SettingsPack pack : category.getPacks()) {
			buffer.writeString(pack.getId());
			buffer.writeBoolean(pack.isLocked());
			
			buffer.writeInt(pack.getSettings().size());
			for (ISetting setting : pack.getSettings()) {
				buffer.writeString(setting.getId());
				buffer.writeBoolean(setting.isLocked());
				
				setting.encode(buffer);
			}
		}
	}
	
	private File getCacheDir() {
		File directory = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), CACHE_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private File getSettingsFolder() {
		File directory = new File(getCacheDir(), SETTINGS_PATH);

		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		return directory;
	}
	
	private File getSettingsFile(SettingsCategory category) {
		return new File(getSettingsFolder(), String.format("%s.%s", category.getName(), FILE_EXTENSION));
	}
}

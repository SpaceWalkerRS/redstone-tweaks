package redstonetweaks.setting.preset;

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
import redstonetweaks.listeners.IPresetListener;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.PresetsPacket;
import redstonetweaks.packet.types.DeletePresetForeverPacket;
import redstonetweaks.packet.types.DeletePresetPacket;
import redstonetweaks.util.PacketUtils;

public class ServerPresetsManager implements IPresetListener {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String PRESETS_PATH = "presets";
	private static final String FILE_EXTENSION = "rtp";
	
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
	public void presetDeleted(Preset preset) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new DeletePresetPacket(preset));
		}
	}
	
	@Override
	public void presetDeletedForever(Preset preset) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new DeletePresetForeverPacket(preset));
		}
	}
	
	@Override
	public void presetAdded(Preset preset) {
		
	}
	
	public void onStartUp() {
		Presets.addListener(this);
		
		loadGlobalPresets();
	}
	
	public void onShutdown() {
		Presets.removeListener(this);
		
		saveGlobalPresets();
		cleanUpPresetFiles();
	}
	
	public void onLoadWorld() {
		loadLocalPresets();
	}
	
	public void onSaveWorld() {
		saveLocalPresets();
	}
	
	public void onPlayerJoined(ServerPlayerEntity player) {
		if (server.isRemote()) {
			sendPresetsToPlayer(player);
		}
	}
	
	private void sendPresetsToPlayer(ServerPlayerEntity player) {
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)server).getPacketHandler();
		PresetsPacket packet = new PresetsPacket(Presets.getAllPresets());
		
		if (player == null) {
			packetHandler.sendPacket(packet);
		} else {
			packetHandler.sendPacketToPlayer(packet, player);
		}
	}
	
	private void loadGlobalPresets() {
		RedstoneTweaks.LOGGER.info("Loading global presets");
		
		loadPresets(false);
	}
	
	private void loadLocalPresets() {
		RedstoneTweaks.LOGGER.info(String.format("Loading presets for \'%s\'", server.getSaveProperties().getLevelName()));
		
		loadPresets(true);
	}
	
	private void loadPresets(boolean local) {
		File directory = getPresetsFolder(local);
		
		deaf = true;
		
		for (File file : directory.listFiles()) {
			if (file.isFile() && file.getName().endsWith(FILE_EXTENSION)) {
				loadPreset(file, local);
			}
		}
		
		deaf = false;
	}
	
	private void loadPreset(File file, boolean local) {
		try (FileInputStream stream = new FileInputStream(file)) {
			byte[] data = IOUtils.toByteArray(stream);
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(data));
			
			readPreset(buffer, local);
		} catch (Exception e) {
			
		}
	}
	
	private void readPreset(PacketByteBuf buffer, boolean local) {
		String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		String description = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		Preset.Mode mode = Preset.Mode.fromIndex(buffer.readByte());
		
		Preset preset = new Preset(name, name, description, mode, local);
		
		preset.decode(buffer);
		
		if (!Presets.register(preset)) {
			Presets.delete(preset);
		}
	}
	
	public void reloadPresets() {
		saveGlobalPresets();
		saveLocalPresets();
		
		Presets.softReset();
		
		loadGlobalPresets();
		loadLocalPresets();
		
		sendPresetsToPlayer(null);
	}
	
	private void saveGlobalPresets() {
		RedstoneTweaks.LOGGER.info("Saving global presets");
		
		savePresets(false);
	}
	
	private void saveLocalPresets() {
		RedstoneTweaks.LOGGER.info(String.format("Saving presets for \'%s\'", server.getSaveProperties().getLevelName()));
		
		savePresets(true);
	}
	
	private void savePresets(boolean local) {
		deaf = true;
		
		for (Preset preset : Presets.getAllPresets()) {
			if (preset.isEditable() && (preset.isLocal() == local) && Presets.isActive(preset)) {
				savePreset(preset);
			}
		}
		
		deaf = false;
	}
	
	private void savePreset(Preset preset) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		writePreset(preset, buffer);
		
		if (buffer.hasArray()) {
			File file = getPresetFile(preset);
			
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
	
	private void writePreset(Preset preset, PacketByteBuf buffer) {
		buffer.writeString(preset.getName());
		buffer.writeString(preset.getDescription());
		buffer.writeByte(preset.getMode().getIndex());
		
		preset.encode(buffer);
	}
	
	private void cleanUpPresetFiles() {
		cleanUpPresetFiles(false);
		cleanUpPresetFiles(true);
	}
	
	private void cleanUpPresetFiles(boolean local) {
		File presetsFolder = getPresetsFolder(local);
		
		for (File file : presetsFolder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(FILE_EXTENSION) && shouldDeleteFile(file, local)) {
				file.delete();
			}
		}
	}
	
	private boolean shouldDeleteFile(File file, boolean local) {
		try (FileInputStream stream = new FileInputStream(file)) {
			byte[] data = IOUtils.toByteArray(stream);
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(data));
			
			String savedName = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
			
			boolean shouldDelete = false;
			
			for (Preset preset : Presets.getAllPresets()) {
				if (preset.isEditable() && (preset.isLocal() == local)) {
					if (Presets.isActive(preset)) {
						if (preset.getName().equals(savedName)) {
							return false;
						}
					} else {
						if (preset.getSavedName().equals(savedName)) {
							shouldDelete = true;
						}
					}
				}
			}
			
			return shouldDelete;
		} catch (Exception e) {
			
		}
		
		return false;
	}
	
	private File getCacheDir(boolean local) {
		File directory = new File(local ? server.getSavePath(WorldSavePath.ROOT).toFile() : server.getRunDirectory(), CACHE_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private File getPresetsFolder(boolean local) {
		File directory = new File(getCacheDir(local), PRESETS_PATH);

		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		return directory;
	}
	
	private File getPresetFile(Preset preset) {
		return new File(getPresetsFolder(preset.isLocal()), String.format("%s.%s", preset.getName(), FILE_EXTENSION));
	}
}

package redstonetweaks.setting.preset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.listeners.IPresetListener;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.PresetsPacket;
import redstonetweaks.packet.types.RemovePresetPacket;
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
	public void presetRemoved(Preset preset) {
		if (!deaf && server.isRemote()) {
			((RTIMinecraftServer)server).getPacketHandler().sendPacket(new RemovePresetPacket(preset));
		}
	}
	
	@Override
	public void presetAdded(Preset preset) {
		
	}
	
	public void onStartUp() {
		loadPresets();
		
		Presets.addListener(this);
	}
	
	public void onShutdown() {
		Presets.removeListener(this);
		
		savePresets();
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
	
	private void loadPresets() {
		RedstoneTweaks.LOGGER.info("Loading presets");
		
		File directory = getPresetsFolder();
		
		for (File file : directory.listFiles()) {
			if (file.isFile() && file.getName().endsWith(FILE_EXTENSION)) {
				loadPreset(file);
			}
		}
	}
	
	private void loadPreset(File file) {
		try (FileInputStream stream = new FileInputStream(file)) {
			byte[] data = IOUtils.toByteArray(stream);
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(data));
			
			readPreset(buffer);
		} catch (Exception e) {
			
		}
	}
	
	private void readPreset(PacketByteBuf buffer) {
		String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		String description = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		Preset.Mode mode = Preset.Mode.fromIndex(buffer.readByte());
		
		Preset preset = new Preset(name, name, description, mode);
		
		if (Presets.register(preset)) {
			preset.decode(buffer);
		}
	}
	
	public void reloadPresets() {
		savePresets();
		
		Presets.reset();
		Presets.init();
		
		loadPresets();
		
		sendPresetsToPlayer(null);
	}
	
	private void savePresets() {
		RedstoneTweaks.LOGGER.info("Saving presets");
		
		cleanUpPresetFiles();
		
		for (Preset preset : Presets.getAllPresets()) {
			if (preset.isEditable()) {
				savePreset(preset);
			}
		}
	}
	
	private void cleanUpPresetFiles() {
		File presetsFolder = getPresetsFolder();
		
		for (File file : presetsFolder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(FILE_EXTENSION) && shouldDeleteFile(file)) {
				file.delete();
			}
		}
		
		Presets.cleanUp();
	}
	
	private boolean shouldDeleteFile(File file) {
		try (FileInputStream stream = new FileInputStream(file)) {
			byte[] data = IOUtils.toByteArray(stream);
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(data));
			
			String savedName = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
			
			for (Preset preset : Presets.getAllPresets()) {
				try {
					if (preset.isEditable() && !Presets.isActive(preset) && preset.getSavedName().equals(savedName)) {
						return true;
					}
				} catch (Exception e) {
					
				}
			}
		} catch (Exception e) {
			
		}
		
		return false;
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
		return new File(getPresetsFolder(), String.format("%s.%s", preset.getName(), FILE_EXTENSION));
	}
}

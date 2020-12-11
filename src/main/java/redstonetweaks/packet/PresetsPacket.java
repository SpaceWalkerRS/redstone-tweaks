package redstonetweaks.packet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.types.ISetting;

public class PresetsPacket extends RedstoneTweaksPacket {
	
	private int presetsCount;
	
	private String[] names;
	private String[] descriptions;
	private Preset.Mode[] modes;
	
	private int[] settingsCounts;
	private ISetting[][] settings;
	private String[][] values;
	
	public PresetsPacket() {
		
	}
	
	public PresetsPacket(List<Preset> presets) {
		presetsCount = 0;
		
		for (Preset preset : presets) {
			if (!preset.isEditable()) {
				continue;
			}
			
			presetsCount++;
		}
		
		names = new String[presetsCount];
		descriptions = new String[presetsCount];
		modes = new Preset.Mode[presetsCount];
		
		settingsCounts = new int[presetsCount];
		settings = new ISetting[presetsCount][];
		values = new String[presetsCount][];
		
		int presetIndex = 0;
		for (Preset preset : presets) {
			if (!preset.isEditable()) {
				continue;
			}
			
			List<ISetting> list = new ArrayList<>();
			for (ISetting setting : Settings.ALL) {
				if (setting.hasPreset(preset)) {
					list.add(setting);
				}
			}
			
			names[presetIndex] = preset.getName();
			descriptions[presetIndex] = preset.getDescription();
			modes[presetIndex] = preset.getMode();
			
			int settingsCount = list.size();
			
			settingsCounts[presetIndex] = settingsCount;
			settings[presetIndex] = new ISetting[settingsCount];
			values[presetIndex] = new String[settingsCount];
			
			int settingIndex = 0;
			for (ISetting setting : list) {
				settings[presetIndex][settingIndex] = setting;
				values[presetIndex][settingIndex] = setting.getPresetValueAsString(preset);
				settingIndex++;
			}
			
			presetIndex++;
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(presetsCount);
		
		for (int presetIndex = 0; presetIndex < presetsCount; presetIndex++) {
			buffer.writeString(names[presetIndex]);
			buffer.writeString(descriptions[presetIndex]);
			buffer.writeShort(modes[presetIndex].getIndex());
			
			int settingsCount = settingsCounts[presetIndex];
			buffer.writeInt(settingsCount);
			
			for (int settingIndex = 0; settingIndex < settingsCount; settingIndex++) {
				buffer.writeString(settings[presetIndex][settingIndex].getId());
				buffer.writeString(values[presetIndex][settingIndex]);
			}
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		presetsCount = buffer.readInt();
		
		names = new String[presetsCount];
		descriptions = new String[presetsCount];
		modes = new Preset.Mode[presetsCount];
		
		settingsCounts = new int[presetsCount];
		settings = new ISetting[presetsCount][];
		values = new String[presetsCount][];
		
		for (int presetIndex = 0; presetIndex < presetsCount; presetIndex++) {
			names[presetIndex] = buffer.readString(MAX_STRING_LENGTH);
			descriptions[presetIndex] = buffer.readString(MAX_STRING_LENGTH);
			modes[presetIndex] = Preset.Mode.fromIndex(buffer.readShort());
			
			int settingsCount = buffer.readInt();
			
			settingsCounts[presetIndex] = settingsCount;
			settings[presetIndex] = new ISetting[settingsCount];
			values[presetIndex] = new String[settingsCount];
			
			for (int settingIndex = 0; settingIndex < settingsCount; settingIndex++) {
				settings[presetIndex][settingIndex] = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
				values[presetIndex][settingIndex] = buffer.readString(MAX_STRING_LENGTH);
			}
		}
	}
	
	@Override
	public void execute(MinecraftServer server) {
		
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer()) {
			for (int presetIndex = 0; presetIndex < presetsCount; presetIndex++) {
				PresetEditor editor = new PresetEditor(names[presetIndex], descriptions[presetIndex], modes[presetIndex]);
				
				int settingsCount = settingsCounts[presetIndex];
				
				for (int settingIndex = 0; settingIndex < settingsCount; settingIndex++) {
					ISetting setting = settings[presetIndex][settingIndex];
					
					if (setting != null) {
						editor.addSetting(setting);
						editor.setValueFromString(setting, values[presetIndex][settingIndex]);
					}
				}
				
				if (editor.canSave()) {
					editor.saveChanges();
				}
			}
		}
		
		((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onPresetsPacketReceived();
	}
}

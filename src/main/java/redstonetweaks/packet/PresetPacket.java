package redstonetweaks.packet;

import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.types.ISetting;

public class PresetPacket extends RedstoneTweaksPacket {
	
	private String previousName;
	private String name;
	private String description;
	private Preset.Mode mode;
	
	private int changedCount;
	private int addedCount;
	private int removedCount;
	private ISetting[] changedSettings;
	private String[] changedValues;
	private ISetting[] addedSettings;
	private String[] addedValues;
	private ISetting[] removedSettings;
	
	public PresetPacket() {
		
	}
	
	public PresetPacket(PresetEditor editor) {
		previousName = editor.getPreviousName();
		name = editor.getName();
		description = editor.getDescription();
		mode = editor.getMode();
		
		Set<ISetting> changed = editor.getChangedSettings();
		Set<ISetting> added = editor.getAddedSettings();
		Set<ISetting> removed = editor.getRemovedSettings();
		
		changedCount = changed.size();
		addedCount = added.size();
		removedCount = removed.size();
		
		changedSettings = new ISetting[changedCount];
		changedValues = new String[changedCount];
		addedSettings = new ISetting[addedCount];
		addedValues = new String[addedCount];
		removedSettings = new ISetting[removedCount];
		
		int index = 0;
		for (ISetting setting : changed) {
			changedSettings[index] = setting;
			changedValues[index] = editor.getValueAsString(setting);
			index++;
		}
		index = 0;
		for (ISetting setting : added) {
			addedSettings[index] = setting;
			addedValues[index] = editor.getValueAsString(setting);
			index++;
		}
		index = 0;
		for (ISetting setting : removed) {
			removedSettings[index] = setting;
			index++;
		}
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(previousName);
		buffer.writeString(name);
		buffer.writeString(description);
		buffer.writeShort(mode.getIndex());
		
		buffer.writeInt(changedCount);
		buffer.writeInt(addedCount);
		buffer.writeInt(removedCount);
		
		for (int index = 0; index < changedCount; index++) {
			buffer.writeString(changedSettings[index].getId());
			buffer.writeString(changedValues[index]);
		}
		for (int index = 0; index < addedCount; index++) {
			buffer.writeString(addedSettings[index].getId());
			buffer.writeString(addedValues[index]);
		}
		for (int index = 0; index < removedCount; index++) {
			buffer.writeString(removedSettings[index].getId());
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		previousName = buffer.readString(MAX_STRING_LENGTH);
		name = buffer.readString(MAX_STRING_LENGTH);
		description = buffer.readString(MAX_STRING_LENGTH);
		mode = Preset.Mode.fromIndex(buffer.readShort());
		
		changedCount = buffer.readInt();
		addedCount = buffer.readInt();
		removedCount = buffer.readInt();
		
		changedSettings = new ISetting[changedCount];
		changedValues = new String[changedCount];
		addedSettings = new ISetting[addedCount];
		addedValues = new String[addedCount];
		removedSettings = new ISetting[removedCount];
		
		for (int index = 0; index < changedCount; index++) {
			changedSettings[index] = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
			changedValues[index] = buffer.readString(MAX_STRING_LENGTH);
		}
		for (int index = 0; index < addedCount; index++) {
			addedSettings[index] = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
			addedValues[index] = buffer.readString(MAX_STRING_LENGTH);
		}
		for (int index = 0; index < removedCount; index++) {
			removedSettings[index] = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
		}
	}
	
	@Override
	public void execute(MinecraftServer server) {
		PresetEditor editor = constructPresetEditor();
		
		if (editor.canSave()) {
			editor.saveChanges();
			
			((RTIMinecraftServer)server).getSettingsManager().getPresetsManager().onPresetPacketReceived(editor);
		} else {
			editor.discardChanges();
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		Preset preset;
		if (!client.isInSingleplayer()) {
			PresetEditor editor = constructPresetEditor();
			
			if (editor.canSave()) {
				editor.saveChanges();
			} else {
				editor.discardChanges();
			}
			
			preset = editor.getPreset();
		} else {
			preset = Presets.fromName(name);
		}
		
		((RTIMinecraftClient)client).getSettingsManager().getPresetsManager().onPresetPacketReceived(preset);
	}
	
	private PresetEditor constructPresetEditor() {
		PresetEditor editor = new PresetEditor(previousName, name, description, mode);
		
		while (changedCount > 0) {
			changedCount--;
			
			ISetting setting = changedSettings[changedCount];
			if (setting != null) {
				editor.addSetting(setting);
				editor.setValueFromString(setting, changedValues[changedCount]);
			}
		}
		while (addedCount > 0) {
			addedCount--;
			
			ISetting setting = addedSettings[addedCount];
			if (setting != null) {
				editor.addSetting(setting);
				editor.setValueFromString(setting, addedValues[addedCount]);
			}
		}
		while (removedCount > 0) {
			removedCount--;
			
			ISetting setting = removedSettings[removedCount];
			if (setting != null) {
				editor.removeSetting(setting);
			}
		}
		
		return editor;
	}
}

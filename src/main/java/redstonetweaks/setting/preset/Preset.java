package redstonetweaks.setting.preset;

import net.minecraft.network.PacketByteBuf;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.PacketUtils;

public class Preset {
	
	private static int idCounter = 0;
	
	private final int id;
	// The name of the preset when it was loaded from file, or null if it was not loaded from file
	private final String savedName;
	private final boolean editable;
	
	private String name;
	private String description;
	private Mode mode;
	private boolean local;
	
	private boolean nameChanged;
	private boolean deleted;
	
	// Only use for initializing built-in presets
	public Preset(String name, String description, Mode mode) {
		this(nextId(), null, false, name, description, mode, false);
	}
	
	// Only use when loading presets from file
	public Preset(String savedName, String name, String description, Mode mode, boolean local) {
		this(nextId(), savedName, true, name, description, mode, local);
	}
	
	public Preset(int id, String savedName, boolean editable, String name, String description, Mode mode, boolean local) {
		this.id = id;
		this.savedName = savedName;
		this.editable = editable;
		
		this.name = name;
		this.description = description;
		this.mode = mode;
		this.local = local;
	}
	
	public static void resetIdCounter() {
		idCounter = 0;
	}
	
	public static int nextId() {
		while (Presets.fromId(idCounter) != null) {
			idCounter++;
		}
		
		return idCounter;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Preset) {
			return id == ((Preset)other).id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getSavedName() {
		return savedName;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		
		nameChanged = !name.equals(savedName);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode newMode) {
		mode = newMode;
	}
	
	public boolean isLocal() {
		return local;
	}
	
	public void setIsLocal(boolean local) {
		this.local = local;
	}
	
	public boolean nameChanged() {
		return nameChanged;
	}
	
	public boolean isDeletedForever() {
		return deleted;
	}
	
	public void markDeletedForever() {
		deleted = true;
	}
	
	public void encode(PacketByteBuf buffer) {
		int count = 0;
		for (ISetting setting : Settings.getSettings()) {
			if (setting.hasPreset(this)) {
				count++;
			}
		}
		
		buffer.writeInt(count);
		
		for (ISetting setting : Settings.getSettings()) {
			if (setting.hasPreset(this)) {
				buffer.writeString(setting.getId());
				setting.encodePreset(buffer, this);
			}
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int count = buffer.readInt();
		
		for (int i = 0; i < count; i++) {
			ISetting setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
			if (setting != null) {
				setting.decodePreset(buffer, this);
			}
		}
	}
	
	public enum Mode {
		SET(0),
		SET_OR_DEFAULT(1);
		
		private static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		
		private Mode(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static Mode fromIndex(int index) {
			if (index < 0) {
				return MODES[MODES.length - 1];
			}
			if (index >= MODES.length) {
				return MODES[0];
			}
			return MODES[index];
		}
		
		public Mode next() {
			return fromIndex(index + 1);
		}
		
		public Mode previous() {
			return fromIndex(index - 1);
		}
	}
}

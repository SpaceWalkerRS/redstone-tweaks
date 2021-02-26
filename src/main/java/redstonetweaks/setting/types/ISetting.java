package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;

public interface ISetting {
	
	public String getId();
	
	public SettingsPack getPack();
	
	public String getName();
	
	public String getDescription();
	
	default boolean opOnly() {
		return getPack().opOnly();
	}
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public boolean isLocked();
	
	public void setLocked(boolean locked);
	
	public boolean isDefault();
	
	public void reset();
	
	public void encode(PacketByteBuf buffer);
	
	public void decode(PacketByteBuf buffer);
	
	public void encodePreset(PacketByteBuf buffer, Preset preset);
	
	public void decodePreset(PacketByteBuf buffer, Preset preset);
	
	public void applyPreset(Preset preset);
	
	public void removePreset(Preset preset);
	
	public void removePresets();
	
	public void copyPresetValue(Preset from, Preset to);
	
	public void copyValueToPreset(Preset preset);
	
	public boolean hasPreset(Preset preset);
	
}

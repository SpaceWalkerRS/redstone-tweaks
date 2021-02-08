package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.util.PacketUtils;
import redstonetweaks.world.common.WorldTickOptions;

public class WorldTickOptionsSetting extends Setting<WorldTickOptions> {
	
	public WorldTickOptionsSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public WorldTickOptions getBackupValue() {
		return new WorldTickOptions();
	}
	
	@Override
	public void write(PacketByteBuf buffer, WorldTickOptions value) {
		PacketUtils.writeWorldTickOptions(buffer, value);
	}
	
	@Override
	public WorldTickOptions read(PacketByteBuf buffer) {
		return PacketUtils.readWorldTickOptions(buffer);
	}
	
	@Override
	public void set(WorldTickOptions newValue) {
		super.set(newValue.copy());
	}
	
	@Override
	public void setPresetValue(Preset preset, WorldTickOptions newValue) {
		super.setPresetValue(preset, newValue.copy());
	}
}

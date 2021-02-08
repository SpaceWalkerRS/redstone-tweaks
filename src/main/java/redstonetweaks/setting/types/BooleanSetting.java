package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;
import redstonetweaks.setting.SettingsPack;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public void write(PacketByteBuf buffer, Boolean value) {
		buffer.writeBoolean(value);
	}
	
	@Override
	public Boolean read(PacketByteBuf buffer) {
		return buffer.readBoolean();
	}
	
	@Override
	public Boolean getBackupValue() {
		return false;
	}
}

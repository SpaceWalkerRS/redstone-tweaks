package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;
import redstonetweaks.setting.SettingsPack;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	protected Boolean getBackupValue() {
		return false;
	}
	
	@Override
	protected void write(PacketByteBuf buffer, Boolean value) {
		buffer.writeBoolean(value);
	}
	
	@Override
	protected Boolean read(PacketByteBuf buffer) {
		return buffer.readBoolean();
	}
}

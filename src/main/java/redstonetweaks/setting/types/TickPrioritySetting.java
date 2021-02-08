package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.TickPriority;

import redstonetweaks.setting.SettingsPack;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public TickPriority getBackupValue() {
		return TickPriority.NORMAL;
	}
	
	@Override
	public void write(PacketByteBuf buffer, TickPriority value) {
		buffer.writeByte(value.getIndex());
	}
	
	@Override
	public TickPriority read(PacketByteBuf buffer) {
		return TickPriority.byIndex(buffer.readByte());
	}
}

package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.TickPriority;

import redstonetweaks.setting.SettingsPack;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	protected TickPriority getBackupValue() {
		return TickPriority.NORMAL;
	}
	
	@Override
	protected void write(PacketByteBuf buffer, TickPriority value) {
		buffer.writeByte(value.getIndex());
	}
	
	@Override
	protected TickPriority read(PacketByteBuf buffer) {
		return TickPriority.byIndex(buffer.readByte());
	}
}

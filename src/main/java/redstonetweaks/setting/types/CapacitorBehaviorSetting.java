package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.block.capacitor.CapacitorBehavior;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.util.PacketUtils;

public class CapacitorBehaviorSetting extends Setting<CapacitorBehavior> {
	
	public CapacitorBehaviorSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	protected CapacitorBehavior getBackupValue() {
		return new CapacitorBehavior();
	}
	
	@Override
	protected void write(PacketByteBuf buffer, CapacitorBehavior value) {
		PacketUtils.writeCapacitorBehavior(buffer, value);
	}
	
	@Override
	protected CapacitorBehavior read(PacketByteBuf buffer) {
		return PacketUtils.readCapacitorBehavior(buffer);
	}
	
	@Override
	protected CapacitorBehavior copy(CapacitorBehavior value) {
		return value.copy();
	}
}

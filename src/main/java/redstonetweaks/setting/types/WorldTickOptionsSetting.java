package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.util.PacketUtils;
import redstonetweaks.world.common.WorldTickOptions;

public class WorldTickOptionsSetting extends Setting<WorldTickOptions> {
	
	public WorldTickOptionsSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	protected WorldTickOptions getBackupValue() {
		return new WorldTickOptions();
	}
	
	@Override
	protected void write(PacketByteBuf buffer, WorldTickOptions value) {
		PacketUtils.writeWorldTickOptions(buffer, value);
	}
	
	@Override
	protected WorldTickOptions read(PacketByteBuf buffer) {
		return PacketUtils.readWorldTickOptions(buffer);
	}
	
	@Override
	protected WorldTickOptions copy(WorldTickOptions value) {
		return value.copy();
	}
}

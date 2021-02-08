package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

import redstonetweaks.setting.SettingsPack;

public class DirectionToBooleanSetting extends ArraySetting<Direction, Boolean> {
	
	public DirectionToBooleanSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public Boolean[] getBackupValue() {
		return new Boolean[] {false, false, false, false, false, false};
	}
	
	@Override
	protected void writeElement(PacketByteBuf buffer, Boolean element) {
		buffer.writeBoolean(element);
	}
	
	@Override
	protected Boolean readElement(PacketByteBuf buffer) {
		return buffer.readBoolean();
	}
	
	@Override
	protected Boolean[] getEmptyArray(int size) {
		return new Boolean[size];
	}
	
	@Override
	public Boolean stringToElement(String string) {
		return Boolean.parseBoolean(string);
	}
	
	@Override
	public int getIndexFromKey(Direction key) {
		return key.getId();
	}
	
	@Override
	public Direction getKeyFromIndex(int index) {
		return Direction.byId(index);
	}
}

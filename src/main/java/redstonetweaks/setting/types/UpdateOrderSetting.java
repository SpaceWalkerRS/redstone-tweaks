package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.util.Directionality;
import redstonetweaks.util.PacketUtils;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderSetting extends Setting<UpdateOrder> {
	
	public UpdateOrderSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public UpdateOrder getBackupValue() {
		return new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL);
	}
	
	@Override
	public void write(PacketByteBuf buffer, UpdateOrder value) {
		PacketUtils.writeUpdateOrder(buffer, value);
	}
	
	@Override
	public UpdateOrder read(PacketByteBuf buffer) {
		return PacketUtils.readUpdateOrder(buffer);
	}
	
	@Override
	public void set(UpdateOrder newValue) {
		super.set(newValue.copy());
	}
	
	@Override
	public void setPresetValue(Preset preset, UpdateOrder newValue) {
		super.setPresetValue(preset, newValue.copy());
	}
}

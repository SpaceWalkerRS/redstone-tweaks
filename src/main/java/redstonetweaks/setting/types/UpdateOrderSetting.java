package redstonetweaks.setting.types;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.util.Directionality;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderSetting extends Setting<UpdateOrder> {
	
	public UpdateOrderSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL));
	}
	
	@Override
	public UpdateOrder stringToValue(String string) {
		return UpdateOrder.parseUpdateOrder(string);
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

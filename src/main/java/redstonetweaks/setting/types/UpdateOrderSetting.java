package redstonetweaks.setting.types;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.util.Directionality;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderSetting extends Setting<UpdateOrder> {
	
	public UpdateOrderSetting(String name, String description) {
		super(name, description, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL));
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

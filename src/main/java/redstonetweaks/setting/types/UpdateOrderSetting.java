package redstonetweaks.setting.types;

import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderSetting extends Setting<UpdateOrder> {
	
	public UpdateOrderSetting(String name, String description, UpdateOrder defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public void setValueFromString(String string) {
		set(UpdateOrder.parseUpdateOrder(string));
	}
	
	@Override
	public void set(UpdateOrder newValue) {
		super.set(newValue.copy());
	}
}

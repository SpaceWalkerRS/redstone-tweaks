package redstonetweaks.setting.types;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.util.Directionality;
import redstonetweaks.util.PacketUtils;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderSetting extends Setting<UpdateOrder> {
	
	public UpdateOrderSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	protected UpdateOrder getBackupValue() {
		return new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL);
	}
	
	@Override
	protected void write(PacketByteBuf buffer, UpdateOrder value) {
		PacketUtils.writeUpdateOrder(buffer, value);
	}
	
	@Override
	protected UpdateOrder read(PacketByteBuf buffer) {
		return PacketUtils.readUpdateOrder(buffer);
	}
	
	@Override
	protected UpdateOrder copy(UpdateOrder value) {
		return value.copy();
	}
}

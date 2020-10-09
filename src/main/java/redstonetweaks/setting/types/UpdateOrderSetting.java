package redstonetweaks.setting.types;

import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.BlockUpdate;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderSetting extends Setting<UpdateOrder> {
	
	public UpdateOrderSetting(String prefix, String name, String description, UpdateOrder defaultValue) {
		super(prefix, name, description, defaultValue);
	}
	
	@Override
	public void setFromText(String text) {
		UpdateOrder order = get();
		order.getBlockUpdates().clear();
		
		String[] args = text.split(", ");
		
		try {
			order.setNotifierOrder(UpdateOrder.NotifierOrder.fromIndex(Integer.parseInt(args[0])));
			order.setOffset(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		} catch (Exception e) {
			
		}
		for (int i = 4; i < args.length; i++) {
			try {
				order.getBlockUpdates().add(textToBlockUpdate(args[i]));
			} catch (Exception e) {
				
			}
		}
	}
	
	@Override
	public String getAsText() {
		String asText = "";
		
		asText += get().getNotifierOrder().getIndex() + ", ";
		asText += get().getOffsetX() + ", ";
		asText += get().getOffsetY() + ", ";
		asText += get().getOffsetZ() + ", ";
		for (BlockUpdate update : get().getBlockUpdates()) {
			asText += blockUpdateToText(update) + ", ";
		}
		
		return asText.substring(0, asText.length() - 2);
	}
	
	@Override
	public void set(UpdateOrder newValue) {
		super.set(newValue.copy());
	}
	
	public BlockUpdate textToBlockUpdate(String text) throws NumberFormatException {
		String[] args = text.split("-");
		
		if (args.length == 3) {
			BlockUpdate.Mode mode = BlockUpdate.Mode.fromIndex(Integer.parseInt(args[0]));
			RelativePos notifier = RelativePos.fromIndex(Integer.parseInt(args[1]));
			RelativePos update = RelativePos.fromIndex(Integer.parseInt(args[2]));
			
			return new BlockUpdate(mode, notifier, update);
		}
		
		throw new IllegalStateException("Incorrect number of arguments to create " + BlockUpdate.class);
	}
	
	public String blockUpdateToText(BlockUpdate update) {
		return update.getMode().getIndex() + "-" + update.getNotifierPos().getIndex() + "-" + update.getUpdatePos().getIndex();
	}
}

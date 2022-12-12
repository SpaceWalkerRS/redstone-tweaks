package redstone.tweaks.g4mespeed.setting.decoder;

import com.g4mesoft.setting.decoder.GSISettingDecoder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.g4mespeed.setting.types.TickPrioritySetting;

public class TickPrioritySettingDecoder implements GSISettingDecoder<TickPrioritySetting> {

	@Override
	public TickPrioritySetting decodeSetting(String name, FriendlyByteBuf buffer) {
		TickPriority value = TickPriority.byValue(buffer.readByte());
		TickPriority defaultValue = TickPriority.byValue(buffer.readByte());
		boolean visibleInGui = buffer.readBoolean();
		boolean enabledInGui = buffer.readBoolean();

		TickPrioritySetting setting = new TickPrioritySetting(name, defaultValue, visibleInGui);

		setting.setValue(value);
		setting.setEnabledInGui(enabledInGui);

		return setting;
	}

	@Override
	public void encodeSetting(FriendlyByteBuf buffer, TickPrioritySetting setting) {
		buffer.writeByte(setting.getValue().getValue());
		buffer.writeByte(setting.getDefaultValue().getValue());
		buffer.writeBoolean(setting.isVisibleInGui());
		buffer.writeBoolean(setting.isEnabledInGui());
	}

	@Override
	public String getTypeString() {
		return "TKPR";
	}

	@Override
	public Class<TickPrioritySetting> getSettingClass() {
		return TickPrioritySetting.class;
	}
}

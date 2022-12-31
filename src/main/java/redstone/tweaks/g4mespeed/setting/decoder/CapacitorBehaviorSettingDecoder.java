package redstone.tweaks.g4mespeed.setting.decoder;

import com.g4mesoft.setting.GSISettingDecoder;

import net.minecraft.network.FriendlyByteBuf;

import redstone.tweaks.g4mespeed.setting.types.CapacitorBehaviorSetting;
import redstone.tweaks.world.level.block.CapacitorBehavior;

public class CapacitorBehaviorSettingDecoder implements GSISettingDecoder<CapacitorBehaviorSetting> {

	@Override
	public CapacitorBehaviorSetting decodeSetting(String name, FriendlyByteBuf buffer) {
		CapacitorBehavior value = new CapacitorBehavior().decode(buffer);
		CapacitorBehavior defaultValue = new CapacitorBehavior().decode(buffer);
		boolean visibleInGui = buffer.readBoolean();
		boolean enabledInGui = buffer.readBoolean();

		CapacitorBehaviorSetting setting = new CapacitorBehaviorSetting(name, defaultValue, visibleInGui);

		setting.setValue(value);
		setting.setEnabledInGui(enabledInGui);

		return setting;
	}

	@Override
	public void encodeSetting(FriendlyByteBuf buffer, CapacitorBehaviorSetting setting) {
		setting.getValue().encode(buffer);
		setting.getDefaultValue().encode(buffer);
		buffer.writeBoolean(setting.isVisibleInGui());
		buffer.writeBoolean(setting.isEnabledInGui());
	}

	@Override
	public String getTypeString() {
		return "CB";
	}

	@Override
	public Class<CapacitorBehaviorSetting> getSettingClass() {
		return CapacitorBehaviorSetting.class;
	}
}

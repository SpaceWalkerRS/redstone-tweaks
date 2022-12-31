package redstone.tweaks.g4mespeed.setting.decoder;

import com.g4mesoft.setting.GSISettingDecoder;

import net.minecraft.network.FriendlyByteBuf;

import redstone.tweaks.g4mespeed.setting.types.QuasiConnectivitySetting;
import redstone.tweaks.world.level.block.QuasiConnectivity;

public class QuasiConnectivitySettingDecoder implements GSISettingDecoder<QuasiConnectivitySetting> {

	@Override
	public QuasiConnectivitySetting decodeSetting(String name, FriendlyByteBuf buffer) {
		QuasiConnectivity value = new QuasiConnectivity().decode(buffer);
		QuasiConnectivity defaultValue = new QuasiConnectivity().decode(buffer);
		boolean visibleInGui = buffer.readBoolean();
		boolean enabledInGui = buffer.readBoolean();

		QuasiConnectivitySetting setting = new QuasiConnectivitySetting(name, defaultValue, visibleInGui);

		setting.setValue(value);
		setting.setEnabledInGui(enabledInGui);

		return setting;
	}

	@Override
	public void encodeSetting(FriendlyByteBuf buffer, QuasiConnectivitySetting setting) {
		setting.getValue().encode(buffer);
		setting.getDefaultValue().encode(buffer);
		buffer.writeBoolean(setting.isVisibleInGui());
		buffer.writeBoolean(setting.isEnabledInGui());
	}

	@Override
	public String getTypeString() {
		return "QC";
	}

	@Override
	public Class<QuasiConnectivitySetting> getSettingClass() {
		return QuasiConnectivitySetting.class;
	}
}

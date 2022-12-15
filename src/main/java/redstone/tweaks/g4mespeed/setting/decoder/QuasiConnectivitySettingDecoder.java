package redstone.tweaks.g4mespeed.setting.decoder;

import java.util.EnumMap;
import java.util.Map;

import com.g4mesoft.setting.GSISettingDecoder;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import redstone.tweaks.g4mespeed.setting.types.QuasiConnectivitySetting;

public class QuasiConnectivitySettingDecoder implements GSISettingDecoder<QuasiConnectivitySetting> {

	@Override
	public QuasiConnectivitySetting decodeSetting(String name, FriendlyByteBuf buffer) {
		Map<Direction, Boolean> value = decodeValue(buffer.readByte());
		Map<Direction, Boolean> defaultValue = decodeValue(buffer.readByte());
		boolean visibleInGui = buffer.readBoolean();
		boolean enabledInGui = buffer.readBoolean();

		QuasiConnectivitySetting setting = new QuasiConnectivitySetting(name, defaultValue, visibleInGui);

		setting.setValue(value);
		setting.setEnabledInGui(enabledInGui);

		return setting;
	}

	@Override
	public void encodeSetting(FriendlyByteBuf buffer, QuasiConnectivitySetting setting) {
		buffer.writeByte(encodeValue(setting.getValue()));
		buffer.writeByte(encodeValue(setting.getDefaultValue()));
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

	private int encodeValue(Map<Direction, Boolean> value) {
		int flags = 0;

		for (Direction dir : Direction.values()) {
			if (value.get(dir)) {
				flags |= 1 << dir.get3DDataValue();
			}
		}

		return flags;
	}

	private Map<Direction, Boolean> decodeValue(int flags) {
		Map<Direction, Boolean> value = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			value.put(dir, (flags & (1 << dir.get3DDataValue())) != 0);
		}

		return value;
	}
}

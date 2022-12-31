package redstone.tweaks.g4mespeed.setting.types;

import com.g4mesoft.setting.GSSetting;

import net.minecraft.core.Direction;

import redstone.tweaks.world.level.block.QuasiConnectivity;

public class QuasiConnectivitySetting extends GSSetting<QuasiConnectivity> {

	private final QuasiConnectivity value;

	public QuasiConnectivitySetting(String name, boolean visibleInGui) {
		this(name, new QuasiConnectivity(), visibleInGui);
	}

	public QuasiConnectivitySetting(String name, Direction defaultEnabledDir, boolean visibleInGui) {
		this(name, new QuasiConnectivity(defaultEnabledDir), visibleInGui);
	}

	public QuasiConnectivitySetting(String name, QuasiConnectivity defaultValue, boolean visibleInGui) {
		super(name, defaultValue, visibleInGui);

		if (defaultValue == null) {
			throw new IllegalArgumentException("defaultValue cannot be null!");
		}

		this.value = new QuasiConnectivity(defaultValue);
	}

	@Override
	public QuasiConnectivity getValue() {
		return value;
	}

	@Override
	public GSSetting<QuasiConnectivity> setValue(QuasiConnectivity value) {
		if (!this.value.equals(value)) {
			this.value.set(value);
			notifyOwnerChange();
		}

		return this;
	}

	@Override
	public boolean isDefaultValue() {
		return value.equals(defaultValue);
	}

	@Override
	public boolean isSameType(GSSetting<?> other) {
		return other instanceof QuasiConnectivitySetting;
	}

	@Override
	public GSSetting<QuasiConnectivity> copySetting() {
		return new QuasiConnectivitySetting(name, new QuasiConnectivity(defaultValue), visibleInGui).setValue(value).setEnabledInGui(isEnabledInGui());
	}
}

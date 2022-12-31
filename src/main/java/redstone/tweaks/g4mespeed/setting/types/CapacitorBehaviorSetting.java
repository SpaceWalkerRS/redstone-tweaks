package redstone.tweaks.g4mespeed.setting.types;

import com.g4mesoft.setting.GSSetting;

import redstone.tweaks.world.level.block.CapacitorBehavior;

public class CapacitorBehaviorSetting extends GSSetting<CapacitorBehavior> {

	private final CapacitorBehavior value;

	public CapacitorBehaviorSetting(String name, boolean visibleInGui) {
		this(name, new CapacitorBehavior(), visibleInGui);
	}

	public CapacitorBehaviorSetting(String name, CapacitorBehavior defaultValue, boolean visibleInGui) {
		super(name, defaultValue, visibleInGui);

		if (defaultValue == null) {
			throw new IllegalArgumentException("defaultValue cannot be null!");
		}

		this.value = new CapacitorBehavior(defaultValue);
	}

	@Override
	public CapacitorBehavior getValue() {
		return value;
	}

	@Override
	public GSSetting<CapacitorBehavior> setValue(CapacitorBehavior value) {
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
		return other instanceof CapacitorBehaviorSetting;
	}

	@Override
	public GSSetting<CapacitorBehavior> copySetting() {
		return new CapacitorBehaviorSetting(name, new CapacitorBehavior(defaultValue), visibleInGui).setValue(value).setEnabledInGui(isEnabledInGui());
	}
}

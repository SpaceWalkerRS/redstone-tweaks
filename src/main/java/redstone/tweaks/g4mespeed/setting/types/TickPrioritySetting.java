package redstone.tweaks.g4mespeed.setting.types;

import com.g4mesoft.setting.GSSetting;

import net.minecraft.world.ticks.TickPriority;

public class TickPrioritySetting extends GSSetting<TickPriority> {

	private TickPriority value;

	public TickPrioritySetting(String name, TickPriority defaultValue, boolean visibleInGui) {
		super(name, defaultValue, visibleInGui);

		this.value = defaultValue;
	}

	@Override
	public TickPriority getValue() {
		return value;
	}

	@Override
	public GSSetting<TickPriority> setValue(TickPriority value) {
		if (value != this.value) {
			this.value = value;
			notifyOwnerChange();
		}

		return this;
	}

	@Override
	public boolean isDefaultValue() {
		return value == defaultValue;
	}

	@Override
	public boolean isSameType(GSSetting<?> other) {
		return other instanceof TickPrioritySetting;
	}

	@Override
	public GSSetting<TickPriority> copySetting() {
		return new TickPrioritySetting(name, defaultValue, visibleInGui).setValue(value).setEnabledInGui(isEnabledInGui());
	}
}

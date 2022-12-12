package redstone.tweaks.g4mespeed.setting.types;

import java.util.EnumMap;
import java.util.Map;

import com.g4mesoft.setting.GSSetting;

import net.minecraft.core.Direction;

public class QuasiConnectivitySetting extends GSSetting<Map<Direction, Boolean>> {

	private final Map<Direction, Boolean> value;

	public QuasiConnectivitySetting(String name, boolean visibleInGui) {
		this(name, buildDefaultMap(null), visibleInGui);
	}

	public QuasiConnectivitySetting(String name, Direction defaultEnabledDir, boolean visibleInGui) {
		this(name, buildDefaultMap(defaultEnabledDir), visibleInGui);
	}

	public QuasiConnectivitySetting(String name, Map<Direction, Boolean> defaultValue, boolean visibleInGui) {
		super(name, defaultValue, visibleInGui);

		if (!isValidMap(defaultValue)) {
			throw new IllegalArgumentException("invalid defaultValue!");
		}

		this.value = new EnumMap<>(defaultValue);
	}

	private static Map<Direction, Boolean> buildDefaultMap(Direction defaultEnabledDir) {
		Map<Direction, Boolean> map = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			map.put(dir, dir == defaultEnabledDir);
		}

		return map;
	}

	private static boolean isValidMap(Map<Direction, Boolean> map) {
		Direction[] directions = Direction.values();

		for (Direction dir : directions) {
			if (map.get(dir) == null) {
				return false;
			}
		}

		return map.size() == directions.length;
	}

	@Override
	public Map<Direction, Boolean> getValue() {
		return value;
	}

	public boolean getValue(Direction dir) {
		return value.get(dir);
	}
	
	@Override
	public GSSetting<Map<Direction, Boolean>> setValue(Map<Direction, Boolean> value) {
		return this;
	}

	public GSSetting<Map<Direction, Boolean>> setValue(Direction dir, boolean value) {
		if (value != this.value.get(dir)) {
			this.value.put(dir, value);
			notifyOwnerChange();
		}

		return this;
	}

	@Override
	public boolean isDefaultValue() {
		for (Direction dir : Direction.values()) {
			if (value.get(dir) != defaultValue.get(dir)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isSameType(GSSetting<?> other) {
		return other instanceof QuasiConnectivitySetting;
	}

	@Override
	public GSSetting<Map<Direction, Boolean>> copySetting() {
		return new QuasiConnectivitySetting(name, new EnumMap<>(defaultValue), visibleInGui).setValue(value).setEnabledInGui(isEnabledInGui());
	}
}

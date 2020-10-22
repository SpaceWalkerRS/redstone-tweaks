package redstonetweaks.setting.types;

import net.minecraft.util.math.Direction;

public abstract class DirectionalSetting<T> extends ArraySetting<T> {

	public DirectionalSetting(String prefix, String name, String description, T[] defaultValue) {
		super(prefix, name, description, defaultValue);
	}
	
	public T get(Direction dir) {
		return get(dir.getId());
	}
	
	public void set(Direction dir, T value) {
		set(dir.getId(), value);
	}
	
	public void reset(Direction dir) {
		reset(dir.getId());
	}
	
	public boolean isDefault(Direction dir) {
		return isDefault(dir.getId());
	}
	
	public T getDefault(Direction dir) {
		return getDefault(dir.getId());
	}
}

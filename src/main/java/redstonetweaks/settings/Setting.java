package redstonetweaks.settings;

public abstract class Setting<T> implements ISetting {
	
	protected final String name;
	protected final String description;
	
	protected final T defaultValue;
	
	protected T value;
	
	public Setting(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public boolean isDefault() {
		return get() == getDefault();
	}
	
	@Override
	public void reset() {
		set(getDefault());
	}
	
	@Override
	public String getAsText() {
		return get().toString();
	}
	
	public T get() {
		return value;
	}
	
	public void set(T newValue) {
		value = newValue;
	}
	
	public T getDefault() {
		return defaultValue;
	}
}

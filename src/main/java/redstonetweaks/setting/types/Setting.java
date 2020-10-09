package redstonetweaks.setting.types;

public abstract class Setting<T> implements ISetting {
	
	private final String id;
	private final String name;
	private final String description;
	
	private final T defaultValue;
	private T value;
	
	public Setting(String prefix, String name, String description, T defaultValue) {
		this.id = prefix + "_" +  name;
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		
		set(getDefault());
	}
	
	@Override
	public String getId() {
		return id;
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
		return get().equals(getDefault());
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

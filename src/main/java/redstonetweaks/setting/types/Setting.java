package redstonetweaks.setting.types;

public abstract class Setting<T> implements ISetting {
	
	private final String name;
	private final String description;
	private final T defaultValue;
	
	private String id;
	private boolean enabled;
	private boolean locked;
	private T value;
	
	public Setting(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.locked = true;
		
		reset();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		if (this.id == null) {
			this.id = id;
		}
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
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	@Override
	public boolean isLocked() {
		return locked;
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
	public String getAsString() {
		return (isLocked() ? '1' : '0') + getValueAsString();
	}
	
	@Override
	public void setFromString(String string) {
		setLocked(string.charAt(0) == '1');
		setValueFromString(string.substring(1));
	}
	
	@Override
	public String getValueAsString() {
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

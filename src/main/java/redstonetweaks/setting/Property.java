package redstonetweaks.setting;

public abstract class Property<T> {
	
	protected final T defaultValue;
	protected final String[] commandSuggestions;
	
	protected T value;
	
	public Property(T defaultValue) {
		this.defaultValue = defaultValue;
		this.commandSuggestions = generateCommandSuggestions();
		
		set(getDefault());
	}
	
	public T get() {
		return value;
	}
	
	public void set(T newValue) {
		value = newValue;
	}
	
	public void reset() {
		set(defaultValue);
	}
	
	public T getDefault() {
		return defaultValue;
	}
	
	public boolean isDefault() {
		return value == getDefault();
	}
	
	public String[] getCommandSuggestions() {
		return commandSuggestions;
	}
	
	protected abstract String[] generateCommandSuggestions();

}
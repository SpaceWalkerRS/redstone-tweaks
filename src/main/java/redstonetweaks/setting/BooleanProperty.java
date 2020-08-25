package redstonetweaks.setting;

public class BooleanProperty extends Property<Boolean> {
	
	public BooleanProperty(boolean defaultValue) {
		super(defaultValue);
	}
	
	protected String[] generateCommandSuggestions() {
		return new String[] {getDefault().toString()};
	}
}
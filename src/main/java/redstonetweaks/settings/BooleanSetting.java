package redstonetweaks.settings;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String name, String description, Boolean defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public void setFromText(String text) {
		try {
			boolean newValue = Boolean.parseBoolean(text);
			set(newValue);
		} catch (Exception e) {
			
		}
	}
}

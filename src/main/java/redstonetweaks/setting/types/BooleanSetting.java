package redstonetweaks.setting.types;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String prefix, String name, String description, Boolean defaultValue) {
		super(prefix, name, description, defaultValue);
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

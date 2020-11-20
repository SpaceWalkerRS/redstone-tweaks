package redstonetweaks.setting.types;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String name, String description, Boolean defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public void setValueFromString(String string) {
		try {
			set(Boolean.parseBoolean(string));
		} catch (Exception e) {
			
		}
	}
}

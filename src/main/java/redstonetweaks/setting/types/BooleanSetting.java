package redstonetweaks.setting.types;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String name, String description) {
		super(name, description, false);
	}
	
	@Override
	public Boolean stringToValue(String string) {
		return Boolean.parseBoolean(string);
	}
}

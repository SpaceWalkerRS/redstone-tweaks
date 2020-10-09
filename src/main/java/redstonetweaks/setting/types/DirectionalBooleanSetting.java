package redstonetweaks.setting.types;

public class DirectionalBooleanSetting extends DirectionalSetting<Boolean> {

	public DirectionalBooleanSetting(String prefix, String name, String description, Boolean[] defaultValue) {
		super(prefix, name, description, defaultValue);
	}

	@Override
	public Boolean textToValue(String text) {
		try {
			return Boolean.parseBoolean(text);
		} catch (Exception e) {
			
		}
		return false;
	}

	@Override
	public String valueToText(Boolean element) {
		return element.toString();
	}
}

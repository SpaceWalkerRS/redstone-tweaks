package redstonetweaks.setting;

public class Setting<T extends Property<?>> {
	
	private final String name;
	
	public Setting(String name) {
		this.name = name;
	}
		public String getName() {
		return name;
	}
}

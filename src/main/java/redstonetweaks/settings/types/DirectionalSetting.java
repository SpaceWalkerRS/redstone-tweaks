package redstonetweaks.settings.types;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.setting.DirectionalSettingWindow;
import redstonetweaks.gui.widget.RTButtonWidget;

public abstract class DirectionalSetting<T> extends ArraySetting<T> {

	public DirectionalSetting(String prefix, String name, String description, T[] defaultValue) {
		super(prefix, name, description, defaultValue);
	}
	
	@Override
	public void populateButtonPanel(ButtonPanel panel) {
		panel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
			panel.screen.openWindow(new DirectionalSettingWindow(panel.screen, this));
		})).alwaysActive());
	}
	
	public T get(Direction dir) {
		return get(dir.getId());
	}
	
	public void set(Direction dir, T value) {
		set(dir.getId(), value);
	}
	
	public void reset(Direction dir) {
		set(dir, getDefault(dir));
	}
	
	public boolean isDefault(Direction dir) {
		return isDefault(dir.getId());
	}
	
	public T getDefault(Direction dir) {
		return getDefault(dir.getId());
	}
	
	public abstract void populateButtonPanel(ButtonPanel panel, Direction direction);
	
}

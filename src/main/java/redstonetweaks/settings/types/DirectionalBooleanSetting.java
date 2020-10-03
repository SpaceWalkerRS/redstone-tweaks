package redstonetweaks.settings.types;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.widget.RTButtonWidget;

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
	
	@Override
	public void populateButtonPanel(ButtonPanel panel, Direction direction) {
		panel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText(valueToText(get(direction))), (button) -> {
			set(direction, !get(direction));
			button.updateMessage();
			panel.doActions();
		}));
	}
}

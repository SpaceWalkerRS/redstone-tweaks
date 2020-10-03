package redstonetweaks.settings.types;

import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.widget.RTButtonWidget;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String prefix, String name, String description, Boolean defaultValue) {
		super(prefix, name, description, defaultValue);
		
		set(getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		try {
			boolean newValue = Boolean.parseBoolean(text);
			set(newValue);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void populateButtonPanel(ButtonPanel panel) {
		panel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText(getAsText()), (button) -> {
			set(!get());
			button.updateMessage();
			panel.doActions();
		}));
	}
}

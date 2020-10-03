package redstonetweaks.settings.types;

import net.minecraft.text.TranslatableText;
import net.minecraft.world.TickPriority;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.widget.RTSliderWidget;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(String prefix, String name, String description, TickPriority defaultValue) {
		super(prefix, name, description, defaultValue);
		
		set(getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		try {
			int index = Integer.parseInt(text);
			set(TickPriority.byIndex(index));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public String getAsText() {
		return String.valueOf(get().getIndex());
	}
	
	@Override
	public void populateButtonPanel(ButtonPanel panel) {
		panel.addButton(new RTSliderWidget(0, 0, 100, 20, 0.0D, () -> new TranslatableText(getAsText()), (slider) -> {
			TickPriority[] priorities = TickPriority.values();
			
			int min = priorities[0].getIndex();
			int steps = (int)Math.round((priorities.length - 1) * slider.getValue());
			
			set(TickPriority.byIndex(min + steps));
			slider.updateMessage();
			panel.doActions();
		}, (slider) -> {
			TickPriority[] priorities = TickPriority.values();
			double steps = get().getIndex() - priorities[0].getIndex();
			slider.setValue(steps / (priorities.length - 1));
		}));
	}
}

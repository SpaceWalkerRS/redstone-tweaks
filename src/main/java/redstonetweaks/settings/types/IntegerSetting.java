package redstonetweaks.settings.types;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.widget.RTTextFieldWidget;

public class IntegerSetting extends Setting<Integer> {
	
	private int minValue;
	private int maxValue;
	
	public IntegerSetting(String prefix, String name, String description, int defaultValue, int minValue, int maxValue) {
		super(prefix, name, description, defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		super.set(getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		try {
			set(Integer.parseInt(text));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void set(Integer newValue) {
		setPrev();
		if (newValue >= getMin() && newValue <= getMax()) {
			super.set(newValue);
		};
	}
	
	@Override
	public void populateButtonPanel(ButtonPanel panel) {
		panel.addButton(new RTTextFieldWidget(panel.screen.getTextRenderer(), 0, 0, 100, 20, (textField) -> {
			textField.setText(getAsText());
		}, (text) -> {
			setFromText(text);
			panel.doActions();
		}));
	}
	
	public int getMin() {
		return minValue;
	}
	
	public int getMax() {
		return maxValue;
	}
}

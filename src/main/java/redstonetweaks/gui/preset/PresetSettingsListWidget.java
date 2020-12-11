package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.TickPriority;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.setting.ArraySettingWindow;
import redstonetweaks.gui.setting.UpdateOrderWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTSliderWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.setting.types.GameModeToBooleanSetting;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.TextFormatting;

public class PresetSettingsListWidget extends RTListWidget<PresetSettingsListWidget.Entry> {
	
	private final PresetsTab parent;
	
	private boolean addSettingsMode;
	private boolean settingsChanged;
	
	public PresetSettingsListWidget(PresetsTab parent, int x, int y, int width, int height) {
		super(parent.screen, x, y, width, height, 22, "Preset Settings");
		
		this.parent = parent;
		this.addSettingsMode = false;
	}
	
	@Override
	protected int getMaxPosition() {
		return (getItemCount() - 1) * itemHeight + headerHeight;
	}
	
	@Override
	protected void initList() {
		for (SettingsPack pack : parent.getSelectedCategory().getSettingsPacks()) {
			List<Entry> settingEntries = new ArrayList<>();
			
			for (ISetting setting : pack.getSettings()) {
				if (addSettingsMode || parent.getPresetEditor().hasSetting(setting)) {
					settingEntries.add(addSettingsMode ? new AddSettingEntry(setting) : new EditSettingEntry(setting));
					
					updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
				}
			}
			
			if (!settingEntries.isEmpty()) {
				addEntry(new SettingsPackEntry(pack));
				
				settingEntries.forEach((entry) -> addEntry(entry));
				
				addEntry(new SeparatorEntry());
			}
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		for (SettingsPack pack : parent.getSelectedCategory().getSettingsPacks()) {
			boolean packMatchesQuery = pack.getName().toLowerCase().contains(query);
			
			List<Entry> settingEntries = new ArrayList<>();
			
			for (ISetting setting : pack.getSettings()) {
				if (addSettingsMode || (parent.getPresetEditor().hasSetting(setting) && (packMatchesQuery || setting.getName().toLowerCase().contains(query)))) {
					settingEntries.add(addSettingsMode ? new AddSettingEntry(setting) : new EditSettingEntry(setting));
					
					updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
				}
			}
			
			if (!settingEntries.isEmpty()) {
				addEntry(new SettingsPackEntry(pack));
				
				settingEntries.forEach((entry) -> addEntry(entry));
				
				addEntry(new SeparatorEntry());
			}
		}
	}
	
	@Override
	public void tick() {
		if (settingsChanged) {
			settingsChanged = false;
			
			filter(parent.getLastSearchQuery());
		}
		
		super.tick();
	}
	
	public boolean addSettingsMode() {
		return addSettingsMode;
	}
	
	public void toggleList() {
		addSettingsMode = !addSettingsMode;
		init();
	}
	
	public class SettingsPackEntry extends Entry {
		
		private final Text title;
		
		public SettingsPackEntry(SettingsPack pack) {
			title = new TranslatableText(pack.getName()).formatted(Formatting.UNDERLINE);
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return Collections.emptyList();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
	}
	
	public class SeparatorEntry extends Entry {
		
		public SeparatorEntry() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return Collections.emptyList();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
	}
	
	public class AddSettingEntry extends Entry {
		
		private final ISetting setting;
		private final Text title;
		private final List<Text> tooltip;
		private final List<RTElement> children;
		private final RTButtonWidget addRemoveButton;
		
		public AddSettingEntry(ISetting setting) {
			this.setting = setting;
			this.title = new TranslatableText(setting.getName());
			this.tooltip = createTooltip(this.setting);
			this.children = new ArrayList<>();
			
			this.addRemoveButton = new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText(parent.getPresetEditor().hasSetting(this.setting) ? "-" : "+"), (button) -> {
				if (parent.getPresetEditor().hasSetting(this.setting)) {
					parent.getPresetEditor().removeSetting(this.setting);
				} else {
					parent.getPresetEditor().addSetting(this.setting);
				}
				
				button.updateMessage();
			});
			this.children.add(this.addRemoveButton);
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			addRemoveButton.setY(y);
			addRemoveButton.render(matrices, mouseX, mouseY, tickDelta);
			
			if (hovered && titleHovered(mouseX, mouseY)) {
				currentTooltip = tooltip;
			}
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int entryTitleWidth) {
			addRemoveButton.setX(getX() + entryTitleWidth + 5);
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
		
		private boolean titleHovered(int mouseX, int mouseY) {
			int width = client.textRenderer.getWidth(title);
			int height = client.textRenderer.fontHeight;
			
			return mouseX >= getX() && mouseX <= getX() + width + 5 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
	}
	
	public class EditSettingEntry extends Entry {
		
		private final ISetting setting;
		private final Text title;
		private final List<Text> tooltip;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		private final RTButtonWidget removeButton;
		
		public EditSettingEntry(ISetting setting) {
			this.setting = setting;
			this.title = new TranslatableText(setting.getName());
			this.tooltip = createTooltip(this.setting);
			this.children = new ArrayList<>();
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanel();
			this.children.add(buttonPanel);
			
			this.removeButton = new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("-"), (button) -> {
				parent.getPresetEditor().removeSetting(this.setting);
				
				settingsChanged = true;
			});
			this.children.add(removeButton);
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			buttonPanel.setY(y);
			buttonPanel.render(matrices, mouseX, mouseY, tickDelta);
			
			removeButton.setY(y);
			removeButton.render(matrices, mouseX, mouseY, tickDelta);
			
			if (hovered && titleHovered(mouseX, mouseY)) {
				currentTooltip = tooltip;
			}
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int entryTitleWidth) {
			buttonPanel.setX(getX() + entryTitleWidth);
			removeButton.setX(buttonPanel.getX() + buttonPanel.getWidth() + 5);
			
			if (!parent.getPresetEditor().isEditable()) {
				buttonPanel.setActive(false);
				removeButton.setActive(false);
			}
		}
		
		@Override
		public void tick() {
			buttonPanel.tick();
		}
		
		@Override
		protected void unfocusTextFields() {
			buttonPanel.unfocusTextFields(null);
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return buttonPanel.focusedIsTextField();
		}
		
		private void populateButtonPanel() {
			if (setting instanceof DirectionToBooleanSetting) {
				DirectionToBooleanSetting dSetting = (DirectionToBooleanSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					ArraySettingWindow<?, ?> window = new ArraySettingWindow<>(screen, dSetting, parent.getPresetEditor().getValue(dSetting), (setting) -> {});
					
					screen.openWindow(window);
					
					if (!parent.getPresetEditor().isEditable()) {
						window.disableButtons();
					}
				})).alwaysActive());
			} else
			if (setting instanceof GameModeToBooleanSetting) {
				GameModeToBooleanSetting gSetting = (GameModeToBooleanSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					ArraySettingWindow<?, ?> window = new ArraySettingWindow<>(screen, gSetting, parent.getPresetEditor().getValue(gSetting), (setting) -> {});
					
					screen.openWindow(window);
					
					if (!parent.getPresetEditor().isEditable()) {
						window.disableButtons();
					}
				})).alwaysActive());
			} else
			if (setting instanceof BooleanSetting) {
				BooleanSetting bSetting = (BooleanSetting)setting;
				buttonPanel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> {
					Formatting color = parent.getPresetEditor().getValue(bSetting) ? Formatting.GREEN : Formatting.RED;
					return new TranslatableText(parent.getPresetEditor().getValueAsString(bSetting)).formatted(color);
				}, (button) -> {
					parent.getPresetEditor().setValue(bSetting, !parent.getPresetEditor().getValue(bSetting));
					
					button.updateMessage();
				}));
			} else
			if (setting instanceof IntegerSetting) {
				IntegerSetting iSetting = (IntegerSetting)setting;
				if (iSetting.getRange() < 10) {
					buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(parent.getPresetEditor().getValueAsString(iSetting)), (slider) -> {
						int min = iSetting.getMin();
						int steps = (int)(slider.getValue() * (iSetting.getRange() + 1));
						
						parent.getPresetEditor().setValue(iSetting, min + steps);
					}, (slider) -> {
						double steps = parent.getPresetEditor().getValue(iSetting) - iSetting.getMin();
						slider.setValue(steps / (iSetting.getRange()));
					}));
				} else {
					buttonPanel.addButton(new RTTextFieldWidget(client.textRenderer, 0, 0, 100, 20, (textField) -> {
						textField.setText(parent.getPresetEditor().getValueAsString(iSetting));
					}, (text) -> {
						parent.getPresetEditor().setValueFromString(iSetting, text);
					}));
				}
			} else
			if (setting instanceof TickPrioritySetting) {
				TickPrioritySetting tSetting = (TickPrioritySetting)setting;
				buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(parent.getPresetEditor().getValueAsString(tSetting)), (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					
					int min = priorities[0].getIndex();
					int steps = (int)Math.round((priorities.length - 1) * slider.getValue());
					
					parent.getPresetEditor().setValue(tSetting, TickPriority.byIndex(min + steps));
				}, (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					double steps = parent.getPresetEditor().getValue(tSetting).getIndex() - priorities[0].getIndex();
					slider.setValue(steps / (priorities.length - 1));
				}));
			} else
			if (setting instanceof UpdateOrderSetting) {
				UpdateOrderSetting uSetting = (UpdateOrderSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					UpdateOrderWindow window = new UpdateOrderWindow(screen, uSetting, parent.getPresetEditor().getValue(uSetting), (setting) -> {});
					
					screen.openWindow(window);
					
					if (!parent.getPresetEditor().isEditable()) {
						window.disableButtons();
					}
				})).alwaysActive());
			}
		}
		
		private boolean titleHovered(int mouseX, int mouseY) {
			int width = client.textRenderer.getWidth(title);
			int height = client.textRenderer.fontHeight;
			
			return mouseX >= getX() && mouseX <= getX() + width + 5 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
	}
	
	public static abstract class Entry extends RTListWidget.Entry<PresetSettingsListWidget.Entry> {
		
		protected List<Text> createTooltip(ISetting setting) {
			List<Text> tooltip = new ArrayList<>();
			for (String line : TextFormatting.getAsLines(setting.getDescription())) {
				tooltip.add(new TranslatableText(line));
			}
			return tooltip;
		}
	}
}
package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.TickPriority;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTLockButtonWidget;
import redstonetweaks.gui.widget.RTSliderWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.gui.widget.RTTexturedButtonWidget;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.ArraySetting;
import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.BugFixSetting;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.TextFormatting;

public class SettingsListWidget extends RTListWidget<SettingsListWidget.Entry> implements ISettingGUIElement {
	
	private static final Map<SettingsCategory, Double> SAVED_SCROLL_AMOUNTS = new HashMap<>();
	
	private final SettingsCategory category;
	
	public SettingsListWidget(RTMenuScreen screen, SettingsCategory category, int x, int y, int width, int height) {
		super(screen, x, y, width, height, 22);
		
		this.category = category;
		
		for (SettingsPack pack : category.getSettingsPacks()) {
			addEntry(new SettingsPackEntry(pack));
			
			for (ISetting setting : pack.getSettings()) {
				addEntry(new SettingEntry(setting));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
			}
			
			addEntry(new SeparatorEntry());
		}
		
		for (Entry entry : children()) {
			entry.init(getEntryTitleWidth());
		}
		
		setScrollAmount(SAVED_SCROLL_AMOUNTS.getOrDefault(category, 0.0D));
	}
	
	@Override
	protected int getMaxPosition() {
		return (getItemCount() - 1) * itemHeight + headerHeight;
	}
	
	@Override
	protected void filterEntries(String query) {
		clearEntries();
		
		for (SettingsPack pack : category.getSettingsPacks()) {
			if (pack.getName().toLowerCase().contains(query)) {
				addEntry(new SettingsPackEntry(pack));
				
				for (ISetting setting : pack.getSettings()) {
					addEntry(new SettingEntry(setting));
					
					updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
				}
				
				addEntry(new SeparatorEntry());
			} else {
				List<Entry> filteredEntries = new ArrayList<>();
				
				for (ISetting setting : pack.getSettings()) {
					if (setting.getName().toLowerCase().contains(query)) {
						filteredEntries.add(new SettingEntry(setting));
						
						updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
					}
				}
				
				if (filteredEntries.size() > 0) {
					addEntry(new SettingsPackEntry(pack));
					
					children().addAll(filteredEntries);
					
					addEntry(new SeparatorEntry());
				}
			}
		}
		
		for (Entry entry : children()) {
			entry.init(getEntryTitleWidth());
		}
	}
	
	@Override
	public void onSettingChanged(ISetting setting) {
		for (Entry entry : children()) {
			if (entry instanceof SettingEntry) {
				SettingEntry settingEntry = (SettingEntry)entry;
				if (settingEntry.setting == setting || setting == null) {
					settingEntry.onSettingChanged();
				}
			}
		}
	}
	
	public void saveScrollAmount() {
		SAVED_SCROLL_AMOUNTS.put(category, getScrollAmount());
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
		public void unfocusTextFields() {
			
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
		public void unfocusTextFields() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
	}
	
	public class SettingEntry extends Entry {
		
		public final ISetting setting;
		private final Text title;
		private final List<Text> tooltip;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		private final RTLockButtonWidget lockButton;
		private final RTButtonWidget resetButton;
		
		public SettingEntry(ISetting setting) {
			this.setting = setting;
			this.title = new TranslatableText(setting.getName());
			this.tooltip = createTooltip();
			this.children = new ArrayList<>();
			
			this.lockButton = new RTLockButtonWidget(0, 0, setting.isLocked(), (button) -> {
				button.toggleLocked();
				
				setting.setLocked(button.isLocked());
				onClientChangeSetting();
			});
			this.children.add(lockButton);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (resetButton) -> {
				setting.reset();
				onClientChangeSetting();
			});
			this.children.add(resetButton);
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanel();
			this.children.add(buttonPanel);
		}
		
		// use hovered to render tooltip
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			buttonPanel.setY(y);
			buttonPanel.render(matrices, mouseX, mouseY, tickDelta);
			
			lockButton.setY(y);
			lockButton.render(matrices, mouseX, mouseY, tickDelta);
			
			resetButton.setY(y);
			resetButton.render(matrices, mouseX, mouseY, tickDelta);
			
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
			lockButton.setX(buttonPanel.getX() + buttonPanel.getWidth() + 5);
			resetButton.setX(lockButton.getX() + lockButton.getWidth() + 2);
			
			updateButtonsActive();
		}
		
		@Override
		public void tick() {
			buttonPanel.tick();
		}
		
		@Override
		public void unfocusTextFields() {
			buttonPanel.unfocusTextFields(null);
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return buttonPanel.focusedIsTextField();
		}
		
		private List<Text> createTooltip() {
			List<Text> tooltip = new ArrayList<>();
			for (String line : TextFormatting.getAsLines(setting.getDescription())) {
				tooltip.add(new TranslatableText(line));
			}
			return tooltip;
		}
		
		private void populateButtonPanel() {
			if (setting instanceof ArraySetting<?, ?>) {
				ArraySetting<?, ?> aSetting = (ArraySetting<?, ?>)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					screen.openWindow(new ArraySettingWindow(screen, category, aSetting));
				})).alwaysActive());
			} else
			if (setting instanceof BooleanSetting) {
				if (setting instanceof BugFixSetting) {
					BugFixSetting bSetting = (BugFixSetting)setting;
					buttonPanel.addButton(new RTButtonWidget(0, 0, 78, 20, () -> {
						Formatting formatting = bSetting.get() ? Formatting.GREEN : Formatting.RED;
						return new TranslatableText(bSetting.getValueAsString()).formatted(formatting);
					}, (button) -> {
						bSetting.set(!bSetting.get());
						onClientChangeSetting();
					}));
					buttonPanel.addButton(new RTTexturedButtonWidget(0, 0, 20, 20, RTTexturedButtonWidget.WIDGETS_LOCATION, 0, 106, 256, 256, 20, (button) -> {
						saveScrollAmount();
						
						String url = bSetting.getBugReportURL();
						client.openScreen(new ConfirmChatLinkScreen((confirm) -> {
							if (confirm) {
								Util.getOperatingSystem().open(url);
							}
							
							client.openScreen(screen);
						}, url, true));
					}).alwaysActive());
				} else {
					BooleanSetting bSetting = (BooleanSetting)setting;
					buttonPanel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> {
						Formatting formatting = bSetting.get() ? Formatting.GREEN : Formatting.RED;
						return new TranslatableText(bSetting.getValueAsString()).formatted(formatting);
					}, (button) -> {
						bSetting.set(!bSetting.get());
						onClientChangeSetting();
					}));
				}
			} else
			if (setting instanceof IntegerSetting) {
				IntegerSetting iSetting = (IntegerSetting)setting;
				if (iSetting.getRange() < 10) {
					buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(iSetting.getValueAsString()), (slider) -> {
						int min = iSetting.getMin();
						int steps = (int)(slider.getValue() * (iSetting.getRange() + 1));
						
						iSetting.set(min + steps);
						onClientChangeSetting();
					}, (slider) -> {
						double steps = iSetting.get() - iSetting.getMin();
						slider.setValue(steps / (iSetting.getRange()));
					}));
				} else {
					buttonPanel.addButton(new RTTextFieldWidget(client.textRenderer, 0, 0, 100, 20, (textField) -> {
						textField.setText(iSetting.getValueAsString());
					}, (text) -> {
						iSetting.setValueFromString(text);
						onClientChangeSetting();
					}));
				}
			} else
			if (setting instanceof TickPrioritySetting) {
				TickPrioritySetting tSetting = (TickPrioritySetting)setting;
				buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(tSetting.getValueAsString()), (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					
					int min = priorities[0].getIndex();
					int steps = (int)Math.round((priorities.length - 1) * slider.getValue());
					
					tSetting.set(TickPriority.byIndex(min + steps));
					onClientChangeSetting();
				}, (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					double steps = tSetting.get().getIndex() - priorities[0].getIndex();
					slider.setValue(steps / (priorities.length - 1));
				}));
			} else
			if (setting instanceof UpdateOrderSetting) {
				UpdateOrderSetting uSetting = (UpdateOrderSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					screen.openWindow(new UpdateOrderWindow(screen, category, uSetting));
				})).alwaysActive());
			}
		}
		
		private void updateButtonsActive() {
			boolean canChangeSettings = ((RTIMinecraftClient)client).getSettingsManager().canChangeSettings();
			boolean canLockSettings = ((RTIMinecraftClient)client).getSettingsManager().canLockSettings();
			
			buttonPanel.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
			lockButton.setActive(canLockSettings && !category.isLocked());
			resetButton.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked() && !setting.isDefault());
		}
		
		private boolean titleHovered(int mouseX, int mouseY) {
			int width = client.textRenderer.getWidth(title);
			int height = client.textRenderer.fontHeight;
			
			return mouseX >= getX() && mouseX <= getX() + width + 5 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
		
		public void onSettingChanged() {
			buttonPanel.updateButtonLabels();
			
			updateButtonsActive();
		}
		
		private void onClientChangeSetting() {
			((RTIMinecraftClient)client).getSettingsManager().onSettingChanged(setting);
		}
	}
	
	public static abstract class Entry extends RTListWidget.Entry<SettingsListWidget.Entry> {
		
		public void init(int entryTitleWidth) {
			
		}
	}
}

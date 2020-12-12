package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.BugFixSetting;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.setting.types.GameModeToBooleanSetting;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.TextFormatting;

public class EditSettingsListWidget extends RTListWidget<EditSettingsListWidget.Entry> {
	
	private final SettingsCategory category;
	
	public EditSettingsListWidget(RTMenuScreen screen, SettingsCategory category, int x, int y, int width, int height) {
		super(screen, x, y, width, height, 22, category.getName());
		
		this.category = category;
	}
	
	@Override
	protected int getMaxPosition() {
		return (getItemCount() - 1) * itemHeight + headerHeight;
	}
	
	@Override
	protected void initList() {
		for (SettingsPack pack : category.getSettingsPacks()) {
			addEntry(new SettingsPackEntry(pack));
			
			for (ISetting setting : pack.getSettings()) {
				addEntry(new SettingEntry(setting));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
			}
			
			addEntry(new SeparatorEntry());
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		for (SettingsPack pack : category.getSettingsPacks()) {
			boolean packMatchesQuery = pack.getName().toLowerCase().contains(query);
			
			List<Entry> settingEntries = new ArrayList<>();
			
			for (ISetting setting : pack.getSettings()) {
				if (packMatchesQuery || setting.getName().toLowerCase().contains(query)) {
					settingEntries.add(new SettingEntry(setting));
					
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
	
	public class SettingEntry extends Entry {
		
		public final ISetting setting;
		private final Text title;
		private final List<Text> tooltip;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		private final RTLockButtonWidget lockButton;
		private final RTButtonWidget resetButton;
		
		private float hoverAnimation;
		
		public SettingEntry(ISetting setting) {
			this.setting = setting;
			this.title = new TranslatableText(setting.getName());
			this.tooltip = createTooltip();
			this.children = new ArrayList<>();
			
			this.lockButton = new RTLockButtonWidget(0, 0, setting.isLocked(), (button) -> {
				button.toggleLocked();
				
				((RTIMinecraftClient)client).getSettingsManager().lockSetting(setting, button.isLocked());
			});
			this.children.add(lockButton);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (resetButton) -> {
				((RTIMinecraftClient)client).getSettingsManager().resetSetting(setting);
			});
			this.children.add(resetButton);
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanel();
			this.children.add(buttonPanel);
			
			this.hoverAnimation = 0.0F;
		}
		
		// use hovered to render tooltip
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (hovered) {
				hoverAnimation = 10.0F - (10.0F - hoverAnimation) / 1.06F;
			} else {
				hoverAnimation = hoverAnimation / 1.4F;
			}
			fillGradient(matrices, 0, y - 1, (int)(hoverAnimation * screen.getWidth() / 10.0F), y + itemHeight - 1, -2146365166, -2146365166);
			
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
		public void init(int titleWidth) {
			buttonPanel.setX(getX() + titleWidth);
			lockButton.setX(buttonPanel.getX() + buttonPanel.getWidth() + 5);
			resetButton.setX(lockButton.getX() + lockButton.getWidth() + 2);
			
			updateButtonsActive();
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
		
		private List<Text> createTooltip() {
			List<Text> tooltip = new ArrayList<>();
			for (String line : TextFormatting.getAsLines(setting.getDescription())) {
				tooltip.add(new TranslatableText(line));
			}
			return tooltip;
		}
		
		private void populateButtonPanel() {
			if (setting instanceof DirectionToBooleanSetting) {
				DirectionToBooleanSetting dSetting = (DirectionToBooleanSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					ArraySettingWindow<?, ?> window = new ArraySettingWindow<>(screen, dSetting, () -> dSetting.get(), (setting) -> changeSetting(dSetting, dSetting.getValueAsString()));
					
					screen.openWindow(window);
					
					if (!((RTIMinecraftClient)client).getSettingsManager().canChangeSettings() || category.isLocked() || setting.isLocked()) {
						window.disableButtons();
					}
				})).alwaysActive());
			} else
			if (setting instanceof GameModeToBooleanSetting) {
				GameModeToBooleanSetting gSetting = (GameModeToBooleanSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					ArraySettingWindow<?, ?> window = new ArraySettingWindow<>(screen, gSetting, () -> gSetting.get(), (setting) -> changeSetting(gSetting, gSetting.getValueAsString()));
					
					screen.openWindow(window);
					
					if (!((RTIMinecraftClient)client).getSettingsManager().canChangeSettings() || category.isLocked() || setting.isLocked()) {
						window.disableButtons();
					}
				})).alwaysActive());
			} else
			if (setting instanceof BooleanSetting) {
				if (setting instanceof BugFixSetting) {
					BugFixSetting bSetting = (BugFixSetting)setting;
					buttonPanel.addButton(new RTButtonWidget(0, 0, 78, 20, () -> {
						Formatting formatting = bSetting.get() ? Formatting.GREEN : Formatting.RED;
						return new TranslatableText(bSetting.getValueAsString()).formatted(formatting);
					}, (button) -> {
						changeSetting(bSetting, bSetting.valueToString(!bSetting.get()));
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
						changeSetting(bSetting, bSetting.valueToString(!bSetting.get()));
					}));
				}
			} else
			if (setting instanceof IntegerSetting) {
				IntegerSetting iSetting = (IntegerSetting)setting;
				if (iSetting.getRange() < 10) {
					buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(iSetting.getValueAsString()), (slider) -> {
						int min = iSetting.getMin();
						int steps = (int)(slider.getValue() * (iSetting.getRange() + 1));
						
						changeSetting(iSetting, iSetting.valueToString(min + steps));
					}, (slider) -> {
						double steps = iSetting.get() - iSetting.getMin();
						slider.setValue(steps / (iSetting.getRange()));
					}));
				} else {
					buttonPanel.addButton(new RTTextFieldWidget(client.textRenderer, 0, 0, 100, 20, (textField) -> {
						textField.setText(iSetting.getValueAsString());
					}, (text) -> {
						changeSetting(iSetting, text);
					}));
				}
			} else
			if (setting instanceof TickPrioritySetting) {
				TickPrioritySetting tSetting = (TickPrioritySetting)setting;
				buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(tSetting.getValueAsString()), (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					
					int min = priorities[0].getIndex();
					int steps = (int)Math.round((priorities.length - 1) * slider.getValue());
					
					changeSetting(tSetting, tSetting.valueToString(TickPriority.byIndex(min + steps)));
				}, (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					double steps = tSetting.get().getIndex() - priorities[0].getIndex();
					slider.setValue(steps / (priorities.length - 1));
				}));
			} else
			if (setting instanceof UpdateOrderSetting) {
				UpdateOrderSetting uSetting = (UpdateOrderSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					UpdateOrderWindow window = new UpdateOrderWindow(screen, uSetting, uSetting.get(), (setting) -> changeSetting(uSetting, uSetting.getValueAsString()));
					
					screen.openWindow(window);
					
					if (!((RTIMinecraftClient)client).getSettingsManager().canChangeSettings() || category.isLocked() || setting.isLocked()) {
						window.disableButtons();
					}
				})).alwaysActive());
			}
		}
		
		private void changeSetting(ISetting setting, String value) {
			((RTIMinecraftClient)client).getSettingsManager().changeSetting(setting, value);
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
	}
	
	public static abstract class Entry extends RTListWidget.Entry<EditSettingsListWidget.Entry> {
		
	}
}

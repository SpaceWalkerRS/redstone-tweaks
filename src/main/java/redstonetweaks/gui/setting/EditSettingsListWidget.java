package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.TickPriority;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTLockButtonWidget;
import redstonetweaks.gui.widget.RTSliderWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.gui.widget.RTTexturedButtonWidget;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.BugFixSetting;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.setting.types.WorldTickOptionsSetting;
import redstonetweaks.util.TextFormatting;

public class EditSettingsListWidget extends RTListWidget<EditSettingsListWidget.Entry> {
	
	private static final Map<SettingsCategory, EditSettingsListWidget.ViewMode> LAST_VIEW_MODES = new HashMap<>();
	
	private final SettingsCategory category;
	private final Predicate<ISetting> modePredicate;
	
	private ViewMode mode;
	
	public EditSettingsListWidget(RTMenuScreen screen, SettingsCategory category, int x, int y, int width, int height) {
		super(screen, x, y, width, height, 22, category.getName());
		
		this.category = category;
		this.modePredicate = (setting) -> {
			if (!setting.isEnabled()) {
				return false;
			}
			if (mode == ViewMode.ALL) {
				return true;
			}
			if (mode == ViewMode.DEFAULT && setting.isDefault()) {
				return true;
			}
			if (mode == ViewMode.CHANGED && !setting.isDefault()) {
				return true;
			}
			
			return false;
		};
		this.mode = LAST_VIEW_MODES.getOrDefault(category, ViewMode.ALL);
	}
	
	@Override
	protected int getMaxPosition() {
		return (getItemCount() - 1) * itemHeight + headerHeight;
	}
	
	@Override
	protected void initList() {
		for (SettingsPack pack : category.getPacks()) {
			List<Entry> settingEntries = new ArrayList<>();
			
			for (ISetting setting : pack.getSettings()) {
				if (modePredicate.test(setting)) {
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
	
	@Override
	protected void filterEntries(String query) {
		for (SettingsPack pack : category.getPacks()) {
			boolean packMatchesQuery = pack.getName().toLowerCase().contains(query);
			
			List<Entry> settingEntries = new ArrayList<>();
			
			for (ISetting setting : pack.getSettings()) {
				if (modePredicate.test(setting) && (packMatchesQuery || setting.getName().toLowerCase().contains(query))) {
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
	
	public boolean canLockSettings() {
		return PermissionManager.canManageSettings(client.player);
	}
	
	public void onSettingChanged(ISetting setting) {
		for (Entry entry : children()) {
			if (entry instanceof SettingEntry) {
				SettingEntry settingEntry = (SettingEntry)entry;
				
				if (settingEntry.setting == setting || setting == null) {
					settingEntry.onSettingChanged();
				}
			} else
			if (entry instanceof SettingsPackEntry) {
				SettingsPackEntry packEntry = (SettingsPackEntry)entry;
				
				if (packEntry.pack.getSettings().contains(setting) || setting == null) {
					packEntry.onSettingChanged();
				}
			}
		}
	}
	
	public ViewMode getMode() {
		return mode;
	}
	
	public void updateMode(boolean next) {
		saveScrollAmount();
		setMode(next ? mode.next() : mode.previous());
	}
	
	public void setMode(ViewMode mode) {
		this.mode = mode;
		
		LAST_VIEW_MODES.put(category, mode);
	}
	
	public static void resetLastModes() {
		LAST_VIEW_MODES.clear();
	}
	
	public class SettingsPackEntry extends Entry {
		
		private final Text title;
		private final SettingsPack pack;
		private final List<RTElement> children;
		private final RTLockButtonWidget lockButton;
		private final RTButtonWidget resetButton;
		
		public SettingsPackEntry(SettingsPack pack) {
			this.title = new TranslatableText(pack.getName()).formatted(Formatting.UNDERLINE);
			this.pack = pack;
			this.children = new ArrayList<>();
			
			this.lockButton = new RTLockButtonWidget(0, 0, this.pack.isLocked(), (button) -> {
				button.toggleLocked();
				
				this.pack.setLocked(button.isLocked());
			});
			this.children.add(lockButton);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (button) -> {
				((RTIMinecraftClient)client).getSettingsManager().resetPack(this.pack, false);
			});
			this.children.add(resetButton);
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			if (lockButton.active) {
				lockButton.setY(y);
				lockButton.render(matrices, mouseX, mouseY, tickDelta);
				
				resetButton.setY(y);
				resetButton.render(matrices, mouseX, mouseY, tickDelta);
			}
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int titleWidth) {
			lockButton.setX(getX() + titleWidth + 105);
			resetButton.setX(lockButton.getX() + lockButton.getWidth() + 2);
			
			updateButtonsActive();
		}
		
		@Override
		public void tick() {
			
		}
		
		private void updateButtonsActive() {
			boolean canManageSettings = PermissionManager.canManageSettings(client.player);
			
			lockButton.setActive(canManageSettings && !category.opOnly());
			resetButton.setActive(canManageSettings && !category.opOnly() && !pack.isDefault());
		}
		
		public void onSettingChanged() {
			updateButtonsActive();
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
			});
			this.children.add(lockButton);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (resetButton) -> {
				((RTIMinecraftClient)client).getSettingsManager().resetSetting(setting, false);
			});
			this.children.add(resetButton);
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanels();
			this.children.add(buttonPanel);
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (hovered) {
				fillGradient(matrices, 2, y - 1, getScrollbarPositionX() - 1, y + itemHeight - 1, -2146365166, -2146365166);
			}
			
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			buttonPanel.setY(y);
			buttonPanel.render(matrices, mouseX, mouseY, tickDelta);
			
			if (lockButton.active) {
				lockButton.setY(y);
				lockButton.render(matrices, mouseX, mouseY, tickDelta);
			}
			
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
			
			updateButtonsActive();
			setResetButtonX();
		}
		
		@Override
		public void tick() {
			buttonPanel.tick();
		}
		
		private List<Text> createTooltip() {
			List<Text> tooltip = new ArrayList<>();
			
			for (String line : TextFormatting.getAsLines(setting.getDescription())) {
				tooltip.add(new TranslatableText(line));
			}
			
			return tooltip;
		}
		
		private void populateButtonPanels() {
			if (setting instanceof DirectionToBooleanSetting) {
				DirectionToBooleanSetting dSetting = (DirectionToBooleanSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					ArraySettingWindow<?, ?> window = new ArraySettingWindow<>(screen, dSetting, () -> dSetting.get(), (setting) -> Settings.settingValueChanged(dSetting));
					
					screen.openWindow(window);
					
					if (!PermissionManager.canChangeSettings(client.player, category) || category.isLocked() || setting.isLocked()) {
						window.disableButtons();
					}
				})).alwaysActive());
			} else
			if (setting instanceof BooleanSetting) {
				if (setting instanceof BugFixSetting) {
					BugFixSetting bSetting = (BugFixSetting)setting;
					buttonPanel.addButton(new RTButtonWidget(0, 0, 78, 20, () -> {
						Formatting formatting = bSetting.get() ? Formatting.GREEN : Formatting.RED;
						return new TranslatableText(String.valueOf(bSetting.get())).formatted(formatting);
					}, (button) -> {
						bSetting.set(!bSetting.get());
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
						return new TranslatableText(String.valueOf(bSetting.get())).formatted(formatting);
					}, (button) -> {
						bSetting.set(!bSetting.get());
					}));
				}
			} else
			if (setting instanceof IntegerSetting) {
				IntegerSetting iSetting = (IntegerSetting)setting;
				if (iSetting.getRange() < 10) {
					buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(String.valueOf(iSetting.get())), (slider) -> {
						int min = iSetting.getMin();
						int steps = (int)(slider.getValue() * (iSetting.getRange() + 1));
						
						iSetting.set(min + steps);
					}, (slider) -> {
						double steps = iSetting.get() - iSetting.getMin();
						slider.setValue(steps / (iSetting.getRange()));
					}));
				} else {
					buttonPanel.addButton(new RTTextFieldWidget(client.textRenderer, 0, 0, 100, 20, (textField) -> {
						textField.setText(String.valueOf(iSetting.get()));
					}, (text) -> {
						try{
							iSetting.set(Integer.parseInt(text));
						} catch (Exception e) {
							
						}
					}));
				}
			} else
			if (setting instanceof TickPrioritySetting) {
				TickPrioritySetting tSetting = (TickPrioritySetting)setting;
				buttonPanel.addButton(new RTSliderWidget(0, 0, 100, 20, () -> new TranslatableText(String.valueOf(tSetting.get())), (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					
					int min = priorities[0].getIndex();
					int steps = (int)Math.round((priorities.length - 1) * slider.getValue());
					
					tSetting.set(TickPriority.byIndex(min + steps));
				}, (slider) -> {
					TickPriority[] priorities = TickPriority.values();
					double steps = tSetting.get().getIndex() - priorities[0].getIndex();
					slider.setValue(steps / (priorities.length - 1));
				}));
			} else
			if (setting instanceof UpdateOrderSetting) {
				UpdateOrderSetting uSetting = (UpdateOrderSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					UpdateOrderWindow window = new UpdateOrderWindow(screen, uSetting, () -> uSetting.get(), (setting) -> Settings.settingValueChanged(uSetting));
					
					screen.openWindow(window);
					
					if (!PermissionManager.canChangeSettings(client.player, category) || category.isLocked() || setting.isLocked()) {
						window.disableButtons();
					}
				})).alwaysActive());
			} else
			if (setting instanceof WorldTickOptionsSetting) {
				WorldTickOptionsSetting wSetting = (WorldTickOptionsSetting)setting;
				buttonPanel.addButton((new RTButtonWidget(0, 0, 100, 20, () -> new TranslatableText("EDIT"), (button) -> {
					WorldTickOptionsWindow window = new WorldTickOptionsWindow(screen, wSetting, () -> wSetting.get(), (setting) -> Settings.settingValueChanged(wSetting));
					
					screen.openWindow(window);
					
					if (!PermissionManager.canChangeSettings(client.player, category) || category.isLocked() || setting.isLocked()) {
						window.disableButtons();
					}
				})).alwaysActive());
			}
		}
		
		private void updateButtonsActive() {
			boolean canChangeSettings = PermissionManager.canChangeSettings(client.player, category);
			boolean canManageSettings = PermissionManager.canManageSettings(client.player);
			boolean locked = setting.isLocked() || setting.getPack().isLocked() || category.isLocked();
			
			buttonPanel.setActive(canChangeSettings && (!locked || canManageSettings));
			lockButton.setActive(canManageSettings && !category.opOnly());
			resetButton.setActive(canChangeSettings && !setting.isDefault() && (!locked || canManageSettings));
		}
		
		private void setResetButtonX() {
			resetButton.setX(lockButton.getX() + (lockButton.active ? lockButton.getWidth() + 2 : 0));
		}
		
		private boolean titleHovered(int mouseX, int mouseY) {
			int width = client.textRenderer.getWidth(title);
			int height = client.textRenderer.fontHeight;
			
			return mouseX >= getX() && mouseX <= getX() + width + 5 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
		
		public void onSettingChanged() {
			buttonPanel.updateButtonLabels();
			
			updateButtonsActive();
			setResetButtonX();
		}
	}
	
	public static abstract class Entry extends RTListWidget.Entry<EditSettingsListWidget.Entry> {
		
	}
	
	public enum ViewMode {
		
		ALL(0),
		DEFAULT(1),
		CHANGED(2);
		
		private static final ViewMode[] MODES;
		
		static {
			MODES = new ViewMode[values().length];
			
			for (ViewMode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		
		private ViewMode(int index) {
			this.index = index;
		}
		
		public static ViewMode fromIndex(int index) {
			if (index < 0) {
				return MODES[MODES.length - 1];
			}
			if (index >= MODES.length) {
				return MODES[0];
			}
			
			return MODES[index];
		}
		
		public ViewMode next() {
			return fromIndex(index + 1);
		}
		
		public ViewMode previous() {
			return fromIndex(index - 1);
		}
	}
}

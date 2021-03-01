package redstonetweaks.gui.setting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import redstonetweaks.gui.ConfirmWindow;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTLockButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.listeners.IPermissionListener;
import redstonetweaks.listeners.ISettingListener;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;

public class SettingsTab extends RTMenuTab implements ISettingListener, IPermissionListener {
	
	private static final int HEADER_HEIGHT = 25;
	private static final Map<SettingsCategory, String> LAST_SEARCH_QUERIES = new HashMap<>();
	
	private final SettingsCategory category;
	
	private EditSettingsListWidget settingsList;
	private RTTextFieldWidget searchBox;
	private RTButtonWidget clearSearchBoxButton;
	private RTButtonWidget viewModeButton;
	private RTLockButtonWidget lockButton;
	private RTButtonWidget resetButton;
	
	public SettingsTab(RTMenuScreen screen, SettingsCategory category) {
		super(screen, new TranslatableText(category.getName()));
		
		this.category = category;
	}
	
	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (getFocused() == null || !getFocused().charTyped(chr, keyCode)) {
			setFocused(searchBox);
			
			return searchBox.charTyped(chr, keyCode);
		}
		
		return true;
	}
	
	@Override
	protected void tickContents() {
		searchBox.tick();
		settingsList.tick();
	}
	
	@Override
	protected void refreshContents() {
		settingsList.filter(searchBox.getText());
	}
	
	@Override
	protected void initContents() {
		settingsList = new EditSettingsListWidget(screen, category, 0, screen.getHeaderHeight() + HEADER_HEIGHT, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - HEADER_HEIGHT - 5);
		settingsList.init();
		addContent(settingsList);
		
		int y = screen.getHeaderHeight();
		
		resetButton = new RTButtonWidget(screen.getWidth() - 50, y, 40, 20, () -> new TranslatableText("RESET"), (button) -> {
			screen.openWindow(new ConfirmWindow(screen, "Are you sure you want to reset all settings in this category?", 300, () -> ((RTIMinecraftClient)screen.client).getSettingsManager().resetCategory(category, false), () -> {}));
		});
		addContent(resetButton);
		
		lockButton = new RTLockButtonWidget(resetButton.getX() - 22, y, category.isLocked(), (button) -> {
			button.toggleLocked();
			
			category.setLocked(button.isLocked());
		});
		addContent(lockButton);
		
		viewModeButton = new RTButtonWidget(lockButton.getX() - 82, y, 80, 20, () -> new TranslatableText(String.format("View: %s", settingsList.getMode())), (button) -> {
			settingsList.updateMode(!Screen.hasShiftDown());
			settingsList.filter(searchBox.getText());
			
			button.updateMessage();
		});
		addContent(viewModeButton);
		
		clearSearchBoxButton = new RTButtonWidget(viewModeButton.getX() - 25, y, 20, 20, () -> new TranslatableText("<"), (button) -> {
			searchBox.setText("");
		});
		addContent(clearSearchBoxButton);
		
		searchBox = new RTTextFieldWidget(screen.getTextRenderer(), 256, 5, y, clearSearchBoxButton.getX() - 7, 20, (textField) -> {}, (query) -> {
			settingsList.filter(query);
			LAST_SEARCH_QUERIES.put(getCategory(), query);
		});
		searchBox.setText(LAST_SEARCH_QUERIES.get(getCategory()));
		addContent(searchBox);
		
		updateButtonsActive();
		updateButtonPlacements();
		
		Settings.addListener(this);
		PermissionManager.addListener(this);
	}
	
	@Override
	public void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		settingsList.render(matrices, mouseX, mouseY, delta);
		searchBox.render(matrices, mouseX, mouseY, delta);
		clearSearchBoxButton.render(matrices, mouseX, mouseY, delta);
		viewModeButton.render(matrices, mouseX, mouseY, delta);
		if (lockButton.active) {
			lockButton.render(matrices, mouseX, mouseY, delta);
			resetButton.render(matrices, mouseX, mouseY, delta);
		}
	}
	
	@Override
	public void onTabClosed() {
		Settings.removeListener(this);
		PermissionManager.removeListener(this);
		
		settingsList.saveScrollAmount();
	}
	
	public void onSettingChanged(ISetting setting) {
		updateButtonsActive();
		updateButtonPlacements();
		
		settingsList.onSettingChanged(setting);
		
		refreshWindows();
	}
	
	public void updateButtonsActive() {
		boolean canManageSettings = PermissionManager.canManageSettings(screen.client.player);
		
		lockButton.setActive(canManageSettings && !category.opOnly());
		resetButton.setActive(canManageSettings && !category.opOnly() && !category.isDefault());
	}
	
	private void updateButtonPlacements() {
		if (lockButton.active) {
			viewModeButton.setX(lockButton.getX() - viewModeButton.getWidth() - 5);
		} else {
			viewModeButton.setX(resetButton.getX() + resetButton.getWidth() - viewModeButton.getWidth());
		}
		
		clearSearchBoxButton.setX(viewModeButton.getX() - clearSearchBoxButton.getWidth() - 2);
		searchBox.setWidth(clearSearchBoxButton.getX() - searchBox.getX() - 2);
	}
	
	public SettingsCategory getCategory() {
		return category;
	}
	
	public static void clearLastSearchQueries() {
		LAST_SEARCH_QUERIES.clear();
	}
	
	@Override
	public void categoryLockedChanged(SettingsCategory category) {
		if (this.category == category) {
			resetButton.setActive(screen.client.player.hasPermissionLevel(2) || !category.isLocked());
		}
	}
	
	@Override
	public void packLockedChanged(SettingsPack pack) {
		
	}
	
	@Override
	public void settingLockedChanged(ISetting setting) {
		
	}
	
	@Override
	public void settingValueChanged(ISetting setting) {
		
	}
	
	@Override
	public void permissionsChanged() {
		onSettingChanged(null);
	}
}

package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.WarningWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.settings.Settings;

public class PresetsTab extends RTMenuTab {
	
	private static String lastSearchQuery = "";
	
	private final List<RTElement> presetsBrowserContent;
	private final List<RTElement> presetEditorContent;
	
	private PresetEditor presetEditor;
	
	private PresetsListWidget presetsList;
	private PresetSettingsListWidget settingsList;
	
	private RTTextFieldWidget searchBox;
	private RTButtonWidget clearSearchBoxButton;
	
	private RTButtonWidget reloadPresetsButton;
	
	private RTButtonWidget saveButton;
	private RTButtonWidget cancelButton;
	private RTButtonWidget propertiesButton;
	private RTButtonWidget warningButton;
	private RTButtonWidget[] categoryButtons;
	private RTButtonWidget viewModeButton;
	private RTButtonWidget toggleListButton;
	
	private int selectedCategoryIndex;
	private SettingsCategory selectedCategory;
	
	public PresetsTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Presets"));
		
		this.presetsBrowserContent = new ArrayList<>();
		this.presetEditorContent = new ArrayList<>();
	}
	
	@Override
	protected List<RTElement> getContents() {
		return isEditingPreset() ? presetEditorContent : presetsBrowserContent;
	}
	
	@Override
	protected void clearContents() {
		super.clearContents();
		presetEditorContent.clear();
		presetsBrowserContent.clear();
	}
	
	@Override
	protected void initContents() {
		presetsList = new PresetsListWidget(this, 0, screen.getHeaderHeight() + 25, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - 25);
		addBrowserContent(presetsList);

		settingsList = new PresetSettingsListWidget(this, 0, screen.getHeaderHeight() + 72, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - 70);
		addEditorContent(settingsList);
		
		
		clearSearchBoxButton = new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("<"), (button) -> {
			searchBox.setText("");
		});
		addBrowserContent(clearSearchBoxButton);
		addEditorContent(clearSearchBoxButton);
		
		searchBox = new RTTextFieldWidget(screen.getTextRenderer(), 256, 0, 0, 0, 20, (textField) -> {}, (query) -> {
			if (isEditingPreset()) {
				settingsList.filter(query);
			} else {
				presetsList.filter(query);
			}
			
			lastSearchQuery = query;
		});
		searchBox.setText(lastSearchQuery);
		addBrowserContent(searchBox);
		addEditorContent(searchBox);
		
		
		reloadPresetsButton = new RTButtonWidget(screen.getWidth() - 110, screen.getHeaderHeight(), 100, 20, () -> new TranslatableText("Reload Presets"), (button) -> {
			((RTIMinecraftClient)screen.client).getPresetsManager().reloadPresets();
		});
		addBrowserContent(reloadPresetsButton);
		
		
		cancelButton = new RTButtonWidget(screen.getWidth() - 60, screen.getHeaderHeight(), 50, 20, () -> new TranslatableText("Cancel"), (button) -> {
			browsePresets();
		});
		addEditorContent(cancelButton);
		
		saveButton = new RTButtonWidget(cancelButton.getX() - 52, cancelButton.getY(), 50, 20, () -> new TranslatableText("Save"), (button) -> {
			if (getPresetEditor().canSave()) {
				savePreset();
			} else {
				screen.openWindow(new InvalidNameWindow(this));
			}
		});
		addEditorContent(saveButton);
		
		propertiesButton = new RTButtonWidget(saveButton.getX() - 105, saveButton.getY(), 100, 20, () -> new TranslatableText("Properties"), (button) -> {
			screen.openWindow(new PresetWindow(this));
		});
		addEditorContent(propertiesButton);
		
		warningButton = new RTButtonWidget(Math.min(propertiesButton.getX() - 85, (screen.getWidth() - 80) / 2), propertiesButton.getY(), 80, 20, () -> new TranslatableText("WARNING").formatted(Formatting.RED), (button) -> {
			screen.openWindow(new WarningWindow(screen, "Someone else has made changes to this preset that will not show up until you close the editor. If you save your changes you might overwrite some of their changes!", 300));
		});
		addEditorContent(warningButton);
		
		int size = 0;
		for (SettingsCategory category : Settings.getCategories()) {
			if (!category.opOnly()) {
				size++;
			}
		}
		categoryButtons = new RTButtonWidget[size];
		
		int i = 0;
		int x = 5;
		int y = propertiesButton.getY() + 22;
		for (SettingsCategory category : Settings.getCategories()) {
			if (category.opOnly()) {
				continue;
			}
			
			int index = i;
			
			Text text = new TranslatableText(category.getName());
			int width = screen.getTextRenderer().getWidth(text) + 10;
			
			categoryButtons[index] = new RTButtonWidget(x, y, width, 20, () -> text, (button) -> {
				categoryButtons[selectedCategoryIndex].setActive(true);
				button.setActive(false);
				
				selectedCategoryIndex = index;
				selectedCategory = category;
				
				settingsList.init();
				searchBox.setText("");
			});
			
			addEditorContent(categoryButtons[index]);
			
			x += width + 5;
			if (x > screen.getWidth() - 10) {
				x = 5;
				y += 22;
			}
			
			i++;
		}
		
		toggleListButton = new RTButtonWidget(screen.getWidth() - 90, categoryButtons[categoryButtons.length - 1].getY() + 25, 80, 20, () -> new TranslatableText(settingsList.addSettingsMode() ? "Edit Settings" : "Add Settings"), (button) -> {
			settingsList.toggleList();
			
			viewModeChanged();
			
			button.updateMessage();
		});
		addEditorContent(toggleListButton);
		
		viewModeButton = new RTButtonWidget(toggleListButton.getX() - 82, toggleListButton.getY(), 80, 20, () -> new TranslatableText(String.format("View: %s", settingsList.getViewMode())), (button) -> {
			settingsList.updateViewMode(!Screen.hasShiftDown());
			settingsList.filter(searchBox.getText());
			
			button.updateMessage();
		});
		addEditorContent(viewModeButton);
		
		
		if (isEditingPreset()) {
			initEditorContent();
		} else {
			initBrowserContent();
		}
	}
	
	@Override
	public void refreshContents() {
		if (isEditingPreset()) {
			settingsList.filter(searchBox.getText());
		} else {
			presetsList.filter(searchBox.getText());
		}
	}
	
	@Override
	protected void tickContents() {
		searchBox.tick();
		
		if (isEditingPreset()) {
			settingsList.tick();
		}
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (isEditingPreset()) {
			screen.client.textRenderer.drawWithShadow(matrices, getPresetEditor().getName(), 6, propertiesButton.getY() + 6, TEXT_COLOR);
			
			searchBox.render(matrices, mouseX, mouseY, delta);
			clearSearchBoxButton.render(matrices, mouseX, mouseY, delta);
			if (settingsList.addSettingsMode()) {
				viewModeButton.render(matrices, mouseX, mouseY, delta);
			}
			toggleListButton.render(matrices, mouseX, mouseY, delta);
			
			warningButton.render(matrices, mouseX, mouseY, delta);
			propertiesButton.render(matrices, mouseX, mouseY, delta);
			saveButton.render(matrices, mouseX, mouseY, delta);
			cancelButton.render(matrices, mouseX, mouseY, delta);
			
			for (RTButtonWidget button : categoryButtons) {
				button.render(matrices, mouseX, mouseY, delta);
			}
			
			settingsList.render(matrices, mouseX, mouseY, delta);
		} else {
			searchBox.render(matrices, mouseX, mouseY, delta);
			clearSearchBoxButton.render(matrices, mouseX, mouseY, delta);
			
			reloadPresetsButton.render(matrices, mouseX, mouseY, delta);
			
			presetsList.render(matrices, mouseX, mouseY, delta);
		}
	}
	
	@Override
	public void onTabClosed() {
		presetsList.saveScrollAmount();
		
		if (isEditingPreset()) {
			getPresetEditor().discardChanges();
		}
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return getFocused() == searchBox || presetsList.focusedIsTextField();
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		if (searchBox != except) {
			searchBox.unFocus();
		}
	}
	
	public boolean isEditingPreset() {
		return getPresetEditor() != null;
	}
	
	public PresetEditor getPresetEditor() {
		return presetEditor;
	}
	
	public void editPreset(Preset preset) {
		editPreset(Presets.editPreset(preset));
	}
	
	public void newPreset(boolean fromSettings) {
		editPreset(Presets.newPreset(fromSettings));
	}
	
	public void editPreset(PresetEditor editor) {
		presetEditor = editor;
		
		initEditorContent();
	}
	
	public void browsePresets() {
		presetEditor = null;
		
		initBrowserContent();
	}
	
	private void addBrowserContent(RTElement content) {
		presetsBrowserContent.add(content);
	}
	
	private void addEditorContent(RTElement content) {
		presetEditorContent.add(content);
	}
	
	private void initBrowserContent() {
		int y = screen.getHeaderHeight();
		
		clearSearchBoxButton.setX(reloadPresetsButton.getX() - clearSearchBoxButton.getWidth() - 5);
		clearSearchBoxButton.setY(y);
		
		searchBox.setX(5);
		searchBox.setY(y);
		searchBox.setWidth(clearSearchBoxButton.getX() - searchBox.getX() - 2);
		searchBox.setText("");
		
		presetsList.init();
		
		updateButtonsActive();
	}
	
	private void initEditorContent() {
		viewModeChanged();
		
		categoryButtons[0].onPress();
		searchBox.setText("");
		
		warningButton.setVisible(false);
		
		settingsList.init();
		settingsList.setListMode(false);
		
		toggleListButton.updateMessage();
		
		updateButtonsActive();
	}
	
	private void viewModeChanged() {
		int x = settingsList.addSettingsMode() ? viewModeButton.getX() : toggleListButton.getX();
		int y = categoryButtons[categoryButtons.length - 1].getY() + 25;
		
		clearSearchBoxButton.setX(x - clearSearchBoxButton.getWidth() - 5);
		clearSearchBoxButton.setY(y);
		
		searchBox.setX(5);
		searchBox.setY(y);
		searchBox.setWidth(clearSearchBoxButton.getX() - searchBox.getX() - 2);
		
		viewModeButton.setActive(settingsList.addSettingsMode());
	}
	
	public void updateButtonsActive() {
		boolean canEditPresets = PermissionManager.canEditPresets();
		boolean editable = isEditingPreset() ? getPresetEditor().isEditable() : false;
		
		reloadPresetsButton.setActive(canEditPresets);
		
		saveButton.setActive(canEditPresets && editable);
		toggleListButton.setActive(canEditPresets && editable);
	}
	
	public SettingsCategory getSelectedCategory() {
		return selectedCategory;
	}
	
	public String getLastSearchQuery() {
		return lastSearchQuery;
	}
	
	public static void clearLastSearchQuery() {
		lastSearchQuery = "";
	}
	
	private void savePreset() {
		PresetEditor editor = getPresetEditor();
		
		((RTIMinecraftClient)screen.client).getPresetsManager().savePreset(editor);
		
		browsePresets();
	}
	
	public void onPresetChanged(Preset preset) {
		if (isEditingPreset()) {
			if (getPresetEditor().getPreset() == preset || preset == null) {
				warningButton.setVisible(true);
			}
		} else {
			presetsList.filter(searchBox.getText());
			
			refreshWindows();
		}
	}
	
	public void onSettingChanged() {
		if (isEditingPreset()) {
			settingsList.updateButtonsActive();
			
			for (RTWindow window : getWindows()) {
				if (window instanceof PresetWindow) {
					((PresetWindow)window).updateButtonsActive();
				}
			}
		} else {
			presetsList.updateButtonsActive();
			
			for (RTWindow window : getWindows()) {
				if (window instanceof RemovedPresetsWindow) {
					((RemovedPresetsWindow)window).updateButtonsActive();
				}
			}
		}
	}
}

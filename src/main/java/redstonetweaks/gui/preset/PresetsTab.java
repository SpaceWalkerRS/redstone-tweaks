package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.TextFormatting;

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
	private RTButtonWidget[] categoryButtons;
	private RTButtonWidget toggleListButton;
	
	private int selectedCategoryIndex;
	private boolean presetChanged;
	private Text[] presetChangedWarning;
	
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
			((RTIMinecraftClient)screen.client).getSettingsManager().getPresetsManager().reloadPresets();
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
				if (!Presets.isNameValid(getPresetEditor().getName())) {
					screen.openWindow(new InvalidNameWindow(this));
				}
			}
		});
		addEditorContent(saveButton);
		
		propertiesButton = new RTButtonWidget(5, saveButton.getY(), 100, 20, () -> new TranslatableText("Properties"), (button) -> {
			screen.openWindow(new PresetWindow(this));
		});
		addEditorContent(propertiesButton);
		
		categoryButtons = new RTButtonWidget[Settings.CATEGORIES.size()];
		for (int i = 0; i < categoryButtons.length; i++) {
			RTButtonWidget previousButton = i > 0 ? categoryButtons[i - 1] : null;
			int index = i;
			
			String text = Settings.CATEGORIES.get(index).getName();
			int x = index > 0 ? previousButton.getX() + previousButton.getWidth() + 5 : 5;
			int y = index > 0 ? previousButton.getY() : propertiesButton.getY() + 25;
			int width = screen.getTextRenderer().getWidth(text) + 10;
			if (x + width > screen.getWidth() - 10) {
				x = 5;
				y += 22;
			}
			
			categoryButtons[index] = new RTButtonWidget(x, y, width, 20, () -> new TranslatableText(text), (button) -> {
				categoryButtons[selectedCategoryIndex].setActive(true);
				button.setActive(false);
				selectedCategoryIndex = index;
				
				settingsList.init();
				searchBox.setText("");
			});
			
			addEditorContent(categoryButtons[index]);
			
			if (index == 0) {
				categoryButtons[index].setActive(false);
			}
		}
		
		toggleListButton = new RTButtonWidget(screen.getWidth() - 90, categoryButtons[categoryButtons.length - 1].getY() + 25, 80, 20, () -> new TranslatableText(settingsList.addSettingsMode() ? "Edit Settings" : "Add Settings"), (button) -> {
			settingsList.toggleList();
			
			button.updateMessage();
		});
		addEditorContent(toggleListButton);
		
		int width = saveButton.getX() - (propertiesButton.getX() + propertiesButton.getWidth());
		int buffer = 10;
		String[] text = TextFormatting.getAsLines(screen.getTextRenderer(), "WARNING: changes have been made to this preset that will not show up until you close the editor!", width - 2 * buffer);
		presetChangedWarning = new Text[text.length];
		for (int index = 0; index < text.length; index++) {
			presetChangedWarning[index] = new TranslatableText(text[index]);
		}
		
		
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
			drawBackGround(matrices);
			
			if (presetChanged) {
				int x = propertiesButton.getX() + propertiesButton.getWidth() + 10;
				int y = propertiesButton.getY() + 10 - 20 * (presetChangedWarning.length / 2);
				
				for (Text line : presetChangedWarning) {
					screen.client.textRenderer.drawWithShadow(matrices, line, x, y, Formatting.RED.getColorValue());
					y += 20;
				}
			}
			
			searchBox.render(matrices, mouseX, mouseY, delta);
			clearSearchBoxButton.render(matrices, mouseX, mouseY, delta);
			toggleListButton.render(matrices, mouseX, mouseY, delta);
			
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
		presetEditor = new PresetEditor(preset);
		
		initEditorContent();
	}
	
	public void newPreset() {
		presetEditor = new PresetEditor("null", "", "", Preset.Mode.SET);
		
		initEditorContent();
	}
	
	public void browsePresets() {
		presetEditor = null;
		presetChanged = false;
		
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
		
		presetsList.init();
	}
	
	private void initEditorContent() {
		int y = categoryButtons[categoryButtons.length - 1].getY() + 25;
		
		clearSearchBoxButton.setX(toggleListButton.getX() - clearSearchBoxButton.getWidth() - 5);
		clearSearchBoxButton.setY(y);
		
		searchBox.setX(5);
		searchBox.setY(y);
		searchBox.setWidth(clearSearchBoxButton.getX() - searchBox.getX() - 2);
		
		saveButton.setActive(getPresetEditor().isEditable());
		
		settingsList.init();
	}
	
	public SettingsCategory getSelectedCategory() {
		return Settings.CATEGORIES.get(selectedCategoryIndex);
	}
	
	public String getLastSearchQuery() {
		return lastSearchQuery;
	}
	
	public static void clearLastSearchQuery() {
		lastSearchQuery = "";
	}
	
	private void savePreset() {
		PresetEditor editor = getPresetEditor();
		
		((RTIMinecraftClient)screen.client).getSettingsManager().getPresetsManager().savePreset(editor);
		
		browsePresets();
	}
	
	private void drawBackGround(MatrixStack matrices) {
		int y = screen.getHeaderHeight() + 22;
		fillGradient(matrices, 0, y, screen.getWidth(), screen.getHeight(), -2146365166, -2146365166);
	}
	
	public void onPresetChanged(Preset preset) {
		if (isEditingPreset()) {
			if (getPresetEditor().getPreset() == preset || preset == null) {
				presetChanged = true;
			}
		} else {
			presetsList.filter(searchBox.getText());
		}
	}
}
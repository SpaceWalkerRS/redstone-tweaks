package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.hotkeys.HotkeysTab;
import redstonetweaks.gui.info.InfoTab;
import redstonetweaks.gui.preset.PresetsTab;
import redstonetweaks.gui.setting.SettingsTab;
import redstonetweaks.gui.widget.IAbstractButtonWidget;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.listeners.IPresetListener;
import redstonetweaks.listeners.ISettingListener;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;

public class RTMenuScreen extends Screen implements ISettingListener, IPresetListener {
	
	private static final int TITLE_MARGIN = 8;
	private static final int TITLE_HEIGHT = 15;
	
	private static int lastOpenedTabIndex = 0;
	
	public final MinecraftClient client;
	private final List<RTMenuTab> tabs;
	private final List<IAbstractButtonWidget> tabButtons;
	
	private int selectedTabIndex;
	private int headerHeight;
	
	public RTMenuScreen(MinecraftClient client) {
		super(new TranslatableText("Redstone Tweaks Menu"));

		this.client = client;
		this.tabs = new ArrayList<>();
		this.tabButtons = new ArrayList<>();
		
		Settings.addListener(this);
		Presets.addListener(this);
	}
	
	@Override
	public Optional<Element> hoveredElement(double mouseX, double mouseY) {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab.isMouseOver(mouseX, mouseY)) {
			return Optional.of(selectedTab);
		}
		
		return super.hoveredElement(mouseX, mouseY);
	}
	
	@Override
	public void setFocused(Element e) {
		Element focused = getFocused();
		
		if (e == focused) {
			return;
		}
		
		if (focused != null && focused instanceof RTElement) {
			((RTElement)focused).unfocus();
		}
		
		super.setFocused(e);
		
		if (e != null && e instanceof RTElement) {
			((RTElement)e).focus();
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab.mouseClicked(mouseX, mouseY, button)) {
			setFocused(selectedTab);
		} else {
			for (IAbstractButtonWidget tabButton : tabButtons) {
				tabButton.mouseClicked(mouseX, mouseY, button);
			}
		}
		
		if (button == 0) {
			setDragging(true);
		}
		
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getSelectedTab().keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (keyCode == 256) {
			onClose();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		
		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, TITLE_MARGIN, 16777215);
		for (IAbstractButtonWidget tabButton : tabButtons) {
			tabButton.render(matrices, mouseX, mouseY, delta);
		}
		
		getSelectedTab().render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void onClose() {
		RTMenuTab selectedTab = getSelectedTab();
		if (!selectedTab.closeTopWindow()) {
			Settings.removeListener(this);
			Presets.removeListener(this);
			
			selectedTab.onTabClosed();
			super.onClose();
		}
	}
	
	@Override
	public List<? extends Element> children() {
		return tabButtons;
	}
	
	@Override
	protected void init() {
		tabs.clear();
		tabButtons.clear();
		
		headerHeight = TITLE_MARGIN + TITLE_HEIGHT + 30;
		
		createTabs();
		createTabButtons();
		
		openTab(lastOpenedTabIndex);
	}
	
	@Override
	public void tick() {
		getSelectedTab().tick();
	}
	
	@Override
	public void resize(MinecraftClient client, int width, int height) {
		getSelectedTab().onTabClosed();
		
		super.resize(client, width, height);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getHeaderHeight() {
		return headerHeight;
	}
	
	public TextRenderer getTextRenderer() {
		return textRenderer;
	}
	
	public void refresh() {
		getSelectedTab().refreshContents();
	}
	
	private void createTabs() {
		for (SettingsCategory category : Settings.getCategories()) {
			tabs.add(new SettingsTab(this, category));
		}
		tabs.add(new PresetsTab(this));
		tabs.add(new HotkeysTab(this));
		tabs.add(new InfoTab(this));
	}
	
	private void createTabButtons() {
		int buttonX = 5;
		int buttonY = TITLE_MARGIN + TITLE_HEIGHT;
		
		for (int index = 0; index < tabs.size(); index++) {
			int tabIndex = index;
			
			Text text = tabs.get(tabIndex).getTitle();
			int buttonWidth = client.textRenderer.getWidth(text) + 10;
			
			tabButtons.add(new RTButtonWidget(buttonX, buttonY, buttonWidth, 20, () -> text, (button) -> {
				switchTab(tabIndex);
			}));
			
			buttonX += buttonWidth + 5;
			if (buttonX > getWidth() - 5) {
				buttonX = 5;
				buttonY += 22;
				
				headerHeight += 22;
			}
		}
	}
	
	private RTMenuTab getSelectedTab() {
		return tabs.get(selectedTabIndex);
	}
	
	private void switchTab(int newIndex) {
		closeSelectedTab();
		openTab(newIndex);
	}
	
	private void closeSelectedTab() {
		tabButtons.get(selectedTabIndex).setActive(true);
		getSelectedTab().onTabClosed();
	}
	
	private void openTab(int index) {
		selectedTabIndex = index;
		lastOpenedTabIndex = index;
		
		tabButtons.get(index).setActive(false);
		getSelectedTab().init();
		
		setFocused(getSelectedTab());
	}
	
	public void openWindow(RTWindow window) {
		getSelectedTab().openWindow(window);
	}
	
	public void closeWindow(RTWindow window) {
		getSelectedTab().closeWindow(window);
	}
	
	public boolean hasWindowOpen() {
		return getSelectedTab().hasWindowOpen();
	}
	
	@Override
	public void categoryLockedChanged(SettingsCategory category) {
		onSettingChanged(null);
	}
	
	@Override
	public void packLockedChanged(SettingsPack pack) {
		onSettingChanged(null);
	}
	
	@Override
	public void settingLockedChanged(ISetting setting) {
		onSettingChanged(setting);
	}
	
	@Override
	public void settingValueChanged(ISetting setting) {
		onSettingChanged(setting);
	}	
	
	@Override
	public void presetChanged(PresetEditor editor) {
		onPresetChanged(editor.getPreset());
	}
	
	@Override
	public void presetRemoved(Preset preset) {
		onPresetChanged(preset);
	}
	
	@Override
	public void presetAdded(Preset preset) {
		onPresetChanged(preset);
	}
	
	public void onSettingChanged(ISetting setting) {
		RTMenuTab selectedTab = getSelectedTab();
		
		if (selectedTab instanceof SettingsTab) {
			((SettingsTab)selectedTab).onSettingChanged(setting);
		} else if (selectedTab instanceof PresetsTab) {
			((PresetsTab)selectedTab).onSettingChanged();
		}
	}
	
	public void onPresetChanged(Preset preset) {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab instanceof PresetsTab) {
			((PresetsTab)selectedTab).onPresetChanged(preset);
		}
	}
	
	public void onHotkeyChanged() {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab instanceof HotkeysTab) {
			((HotkeysTab)selectedTab).onHotkeyChanged();
		}
	}
	
	public static void resetLastOpenedTabIndex() {
		lastOpenedTabIndex = 0;
	}
	
	public static void clearLastSearchQueries() {
		PresetsTab.clearLastSearchQuery();
		SettingsTab.clearLastSearchQueries();
	}
}

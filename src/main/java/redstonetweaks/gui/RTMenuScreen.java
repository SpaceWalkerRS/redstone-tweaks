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
import redstonetweaks.gui.setting.SettingsTab;
import redstonetweaks.gui.widget.IAbstractButtonWidget;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.hotkeys.HotKeyManager;
import redstonetweaks.hotkeys.RTKeyBinding;
import redstonetweaks.setting.types.ISetting;

public class RTMenuScreen extends Screen {
	
	private static final int TITLE_MARGIN = 8;
	private static final int TITLE_HEIGHT = 15;
	
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab.mouseClicked(mouseX, mouseY, button)) {
			setFocused(selectedTab);
		} else {
			for (IAbstractButtonWidget tabButton : tabButtons) {
				if (tabButton.mouseClicked(mouseX, mouseY, button)) {
					setFocused(tabButton);
				}
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
		if (keyCode == 256 || (HotKeyManager.TOGGLE_MENU.matchesKey(keyCode, scanCode) && !focusedIsTextField())) {
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
		
		selectedTabIndex = 0;
		getSelectedTab().init();
		tabButtons.get(selectedTabIndex).setActive(false);
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
	
	private void createTabs() {
		tabs.add(new SettingsTab(this));
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
		tabButtons.get(selectedTabIndex).setActive(true);
		tabButtons.get(newIndex).setActive(false);
		
		selectedTabIndex = newIndex;
		getSelectedTab().init();
	}
	
	public void openWindow(RTWindow window) {
		getSelectedTab().openWindow(window);
	}
	
	public void closeWindow(RTWindow window) {
		getSelectedTab().closeWindow(window);
	}
	
	public boolean focusedIsTextField() {
		return getSelectedTab().focusedIsTextField();
	}
	
	public void onSettingChanged(ISetting setting) {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab instanceof SettingsTab) {
			((SettingsTab)selectedTab).onSettingChanged(setting);
		}
	}
	
	public void onHotkeyChanged(RTKeyBinding keyBinding) {
		RTMenuTab selectedTab = getSelectedTab();
		if (selectedTab instanceof HotkeysTab) {
			((HotkeysTab)selectedTab).onHotkeyChanged(keyBinding);
		}
	}
}

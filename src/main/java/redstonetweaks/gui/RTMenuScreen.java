package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.hotkeys.HotkeysTab;
import redstonetweaks.gui.setting.SettingsTab;
import redstonetweaks.gui.widget.IAbstractButtonWidget;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.hotkeys.RTKeyBinding;
import redstonetweaks.setting.types.ISetting;

public class RTMenuScreen extends Screen {
	
	private static final int TITLE_MARGIN = 8;
	private static final int TITLE_HEIGHT = 15;
	
	public final MinecraftClient client;
	private final List<RTMenuTab> tabs;
	private final List<IAbstractButtonWidget> tabButtons;
	
	private int selectedTabIndex;
	private RTMenuTab selectedTab;
	private boolean switchedTabs;
	private int headerHeight;
	
	public RTMenuScreen(MinecraftClient client) {
		super(new TranslatableText("Redstone Tweaks Menu"));

		this.client = client;
		this.tabs = new ArrayList<>();
		this.tabButtons = new ArrayList<>();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Element el : children()) {
			if (el.mouseClicked(mouseX, mouseY, button)) {
				setFocused(el);
			}
		}
		
		if (button == 0) {
			setDragging(true);
		}
		
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (selectedTab instanceof HotkeysTab && selectedTab.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		
		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, TITLE_MARGIN, 16777215);
		for (IAbstractButtonWidget tabButton : tabButtons) {
			tabButton.render(matrices, mouseX, mouseY, delta);
		}
		selectedTab.render(matrices, mouseX, mouseY, delta);
		
		super.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void onClose() {
		if (!selectedTab.closeTopWindow()) {
			selectedTab.onTabClosed();
			super.onClose();
		}
	}
	
	@Override
	protected void init() {
		tabs.clear();
		tabButtons.clear();
		
		headerHeight = TITLE_MARGIN + TITLE_HEIGHT + 30;
		createTabs();
		
		children.addAll(tabButtons);
		openTab(tabs.get(0));
		tabButtons.get(0).setActive(false);
	}
	
	@Override
	public void tick() {
		if (switchedTabs) {
			closeSelectedTab();
			openTab(tabs.get(selectedTabIndex));
			
			switchedTabs = false;
		}
		
		selectedTab.tick();
	}
	
	@Override
	public void resize(MinecraftClient client, int width, int height) {
		selectedTab.onTabClosed();
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
		addTab(new SettingsTab(this));
		addTab(new HotkeysTab(this));
	}
	
	private void addTab(RTMenuTab tab) {
		int tabIndex = tabs.size();
		
		int buttonWidth = client.textRenderer.getWidth(tab.getTitle()) + 10;
		int buttonX = 5;
		int buttonY = TITLE_MARGIN + TITLE_HEIGHT;
		if (tabIndex > 0) {
			IAbstractButtonWidget prevButton = tabButtons.get(tabButtons.size() - 1);
			buttonX += prevButton.getX() + prevButton.getWidth();
			buttonY = prevButton.getY();
			
			if (buttonX + buttonWidth > width - 5) {
				buttonX = 5;
				buttonY += 22;
				
				headerHeight += 22;
			}
		}
		
		tabs.add(tab);
		tabButtons.add(new RTButtonWidget(buttonX, buttonY, buttonWidth, 20, () -> tab.getTitle(), (button) -> {
			switchTab(tabIndex);
		}));
	}
	
	private void switchTab(int index) {
		tabButtons.get(selectedTabIndex).setActive(true);
		tabButtons.get(index).setActive(false);
		
		selectedTabIndex = index;
		switchedTabs = true;
	}
	
	private void openTab(RTMenuTab tab) {
		selectedTab = tab;
		children.add(tab);
		tab.init();
	}
	
	private void closeSelectedTab() {
		selectedTab.onTabClosed();
		children.remove(selectedTab);
	}
	
	public void openWindow(RTWindow window) {
		selectedTab.openWindow(window);
	}
	
	public void closeWindow(RTWindow window) {
		selectedTab.closeWindow(window);
	}
	
	public boolean focusedIsTextField() {
		if (getFocused() instanceof RTTextFieldWidget && ((RTTextFieldWidget)getFocused()).isActive()) {
			return true;
		}
		return selectedTab.focusedIsTextField();
	}
	
	public void onSettingChanged(ISetting setting) {
		if (selectedTab instanceof SettingsTab) {
			((SettingsTab)selectedTab).onSettingChanged(setting);
		}
	}
	
	public void onHotkeyChanged(RTKeyBinding keyBinding) {
		if (selectedTab instanceof HotkeysTab) {
			((HotkeysTab)selectedTab).onHotkeyChanged(keyBinding);
		}
	}
}

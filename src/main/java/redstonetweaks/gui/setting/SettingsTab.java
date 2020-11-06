package redstonetweaks.gui.setting;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class SettingsTab extends RTMenuTab implements ISettingGUIElement {
	
	private static final int HEADER_HEIGHT = 25;
	
	private static String lastSearchQuery;
	
	private SettingsListWidget settingsList;
	private RTTextFieldWidget searchBox;
	private RTButtonWidget resetButton;
	private RTButtonWidget clearSearchBoxButton;
	
	public SettingsTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Settings"));
	}
	
	@Override
	protected void tickContents() {
		searchBox.tick();
		settingsList.tick();
	}
	
	@Override
	protected void initContents() {
		settingsList = new SettingsListWidget(screen, 0, screen.getHeaderHeight() + HEADER_HEIGHT, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - HEADER_HEIGHT - 5);
		addContent(settingsList);
		
		resetButton = new RTButtonWidget(screen.getWidth() - 50, screen.getHeaderHeight(), 40, 20, () -> new TranslatableText("RESET"), (button) -> {
			Settings.reset();
			((RTIMinecraftClient)screen.client).getSettingsManager().onSettingsReset();
		});
		resetButton.setActive(((RTIMinecraftClient)screen.client).getSettingsManager().canChangeSettings());
		addContent(resetButton);
		
		clearSearchBoxButton = new RTButtonWidget(resetButton.getX() - 25, resetButton.getY(), 20, 20, () -> new TranslatableText("<"), (button) -> {
			searchBox.setText("");
		});
		addContent(clearSearchBoxButton);
		
		searchBox = new RTTextFieldWidget(screen.getTextRenderer(), 256, 5, resetButton.getY(), clearSearchBoxButton.getX() - 10, 20, (textField) -> {}, (query) -> {
			settingsList.filter(query);
			lastSearchQuery = query;
		});
		searchBox.setText(lastSearchQuery);
		addContent(searchBox);
	}
	
	@Override
	public void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		settingsList.render(matrices, mouseX, mouseY, delta);
		searchBox.render(matrices, mouseX, mouseY, delta);
		resetButton.render(matrices, mouseX, mouseY, delta);
		clearSearchBoxButton.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void onTabClosed() {
		settingsList.saveScrollAmount();
	}
	
	@Override
	public void onSettingChanged(ISetting setting) {
		settingsList.onSettingChanged(setting);
		
		for (RTWindow window : windows) {
			if (window instanceof ISettingGUIElement) {
				((ISettingGUIElement)window).onSettingChanged(setting);
			}
		}
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		if (searchBox != except) {
			searchBox.unFocus();
		}
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return settingsList.focusedIsTextField();
	}
}

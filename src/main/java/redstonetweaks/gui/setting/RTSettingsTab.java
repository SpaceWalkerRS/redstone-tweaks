package redstonetweaks.gui.setting;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.helper.MinecraftClientHelper;

public class RTSettingsTab extends RTMenuTab {
	
	private static final int HEADER_HEIGHT = 25;
	
	private static String lastSearchQuery;
	
	private RTSettingsListWidget settingsList;
	private RTTextFieldWidget searchBox;
	private RTButtonWidget resetButton;
	
	public RTSettingsTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Settings"));
	}
	
	@Override
	protected void tickContents() {
		searchBox.tick();
		settingsList.tick();
	}
	
	@Override
	protected void initContents() {
		settingsList = new RTSettingsListWidget(screen, 0, screen.getHeaderHeight() + HEADER_HEIGHT, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - HEADER_HEIGHT);
		addContent(settingsList);
		
		resetButton = new RTButtonWidget(screen.getWidth() - 50, screen.getHeaderHeight(), 40, 20, () -> new TranslatableText("RESET"), (button) -> {
			settingsList.reset();
		});
		resetButton.setActive(((MinecraftClientHelper)screen.client).getSettingsManager().canChangeSettings());
		addContent(resetButton);
		
		searchBox = new RTTextFieldWidget(screen.getTextRenderer(), 5, screen.getHeaderHeight(), screen.getWidth() - resetButton.getWidth() - 20, 20, (textField) -> {}, (query) -> {
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
	}
	
	@Override
	public void onTabClosed() {
		settingsList.saveScrollAmount();
	}
}

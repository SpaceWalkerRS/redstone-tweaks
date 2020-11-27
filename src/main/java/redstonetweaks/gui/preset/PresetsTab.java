package redstonetweaks.gui.preset;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.widget.RTTextFieldWidget;

public class PresetsTab extends RTMenuTab {
	
	private static final int HEADER_HEIGHT = 25;
	
	private static String lastSearchQuery = "";
	
	private PresetsListWidget presetsList;
	private RTTextFieldWidget searchBox;
	
	public PresetsTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Presets"));
	}
	
	@Override
	protected void initContents() {
		presetsList = new PresetsListWidget(screen, 0, screen.getHeaderHeight() + HEADER_HEIGHT, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - HEADER_HEIGHT - 5);
		presetsList.init();
		addContent(presetsList);
		
		int y = screen.getHeaderHeight();
		
		searchBox = new RTTextFieldWidget(screen.getTextRenderer(), 256, 5, y, screen.getWidth(), 20, (textField) -> {}, (query) -> {
			presetsList.filter(query);
			lastSearchQuery = query;
		});
		searchBox.setText(lastSearchQuery);
		addContent(searchBox);
	}
	
	@Override
	protected void tickContents() {
		searchBox.tick();
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		presetsList.render(matrices, mouseX, mouseY, delta);
		searchBox.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void onTabClosed() {
		presetsList.saveScrollAmount();
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return presetsList.focusedIsTextField();
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		
	}
}

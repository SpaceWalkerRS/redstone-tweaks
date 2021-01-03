package redstonetweaks.gui.preset;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTWindow;

public class RemovedPresetsWindow extends RTWindow {
	
	private RemovedPresetsListWidget list;
	
	public RemovedPresetsWindow(PresetsTab parent) {
		super(parent.screen, new TranslatableText("Removed Presets"), 0, 0, 0, 0);
	}
	
	@Override
	protected void initContents() {
		setWidth(screen.getWidth() - 100);
		setHeight(screen.getHeight() - screen.getHeaderHeight() - 50);
		
		setX((screen.getWidth() - getWidth()) / 2);
		setY((screen.getHeight() + screen.getHeaderHeight() - getHeight()) / 2);
		
		setHeaderHeight(36);
		
		list = new RemovedPresetsListWidget(this, getX() + 2, getY() + getHeaderHeight(), getWidth() - 4, getHeight() - getHeaderHeight() - 20);
		list.init();
		addContent(list);
	}
	
	@Override
	protected void tickContents() {
		list.tick();
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		list.render(matrices, mouseX, mouseY, delta);
		
		drawBackgroundTextureBelow(matrices, list.getY() + list.getHeight() + 5, mouseX, mouseY, delta);
	}
	
	@Override
	protected void onRefresh() {
		list.saveScrollAmount();
	}

	@Override
	protected boolean hasFocusedTextField() {
		return list.focusedIsTextField();
	}
	
	public void updateButtonsActive() {
		list.updateButtonsActive();
	}
}

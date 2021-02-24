package redstonetweaks.gui.preset;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTWindow;

public class InvalidNameWindow extends RTWindow {
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 31;
	
	public InvalidNameWindow(PresetsTab parent) {
		super(parent.screen, new TranslatableText("Invalid preset name. Please choose a different preset name!"), (parent.screen.getWidth() - WIDTH) / 2, (parent.screen.getHeight() + parent.screen.getHeaderHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
	}
	
	@Override
	protected void initContents() {
		
	}
	
	@Override
	protected void tickContents() {
		
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		
	}
	
	@Override
	protected void onRefresh() {
		
	}
}

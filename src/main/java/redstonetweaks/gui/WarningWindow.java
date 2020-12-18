package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import redstonetweaks.gui.preset.PresetsTab;
import redstonetweaks.util.TextFormatting;

public class WarningWindow extends RTWindow {
	
	private final List<Text> warning;
	
	public WarningWindow(PresetsTab parent, String text, int width) {
		super(parent.screen, new TranslatableText("WARNING").formatted(Formatting.RED), (parent.screen.getWidth() - width) / 2, 0, width, 0);
		
		this.warning = new ArrayList<>();
		for (String line : TextFormatting.getAsLines(parent.screen.getTextRenderer(), text, width - 30)) {
			warning.add(new TranslatableText(line));
		}
		
		this.setHeight(45 + this.warning.size() * 14);
		this.setY((parent.screen.getHeight() + parent.screen.getHeaderHeight() - this.getHeight()) / 2);
	}
	
	@Override
	protected void initContents() {
		
	}
	
	@Override
	protected void tickContents() {
		
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		for (int index = 0; index < warning.size(); index++) {
			Text line = warning.get(index);
			drawCenteredText(matrices, screen.getTextRenderer(), line, getX() + getWidth() / 2, getY() + 36 + index * 14, TEXT_COLOR);
		}
	}
	
	@Override
	protected void onRefresh() {
		
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return false;
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		
	}
}

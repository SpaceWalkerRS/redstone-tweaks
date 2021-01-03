package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.util.TextFormatting;

public class ConfirmWindow extends RTWindow {
	
	private final List<Text> message;
	private final ConfirmAction onConfirmed;
	private final DenyAction onDenied;
	
	private RTButtonWidget confirmButton;
	private RTButtonWidget denyButton;
	
	public ConfirmWindow(RTMenuScreen screen, String text, int width, ConfirmAction onConfirmed, DenyAction onDenied) {
		super(screen, new TranslatableText("Hold on...").formatted(Formatting.RED), (screen.getWidth() - width) / 2, 0, width, 0);
		
		this.message = new ArrayList<>();
		for (String line : TextFormatting.getAsLines(screen.getTextRenderer(), text, width - 50)) {
			message.add(new TranslatableText(line));
		}
		this.onConfirmed = onConfirmed;
		this.onDenied = onDenied;
		
		this.setHeight(36 + this.message.size() * 14 + 34);
		this.setY((screen.getHeight() + screen.getHeaderHeight() - this.getHeight()) / 2);
	}
	
	@Override
	protected void initContents() {
		int centerX = getX() + getWidth() / 2;
		int y = getY() + getHeight() - 30;
		int spacing = 10;
		
		confirmButton = new RTButtonWidget(centerX - spacing - 80, y, 80, 20, () -> new TranslatableText("Yes"), (button) -> {
			onConfirmed.confirm();
			
			screen.closeWindow(this);
		});
		addContent(confirmButton);
		
		denyButton = new RTButtonWidget(centerX + spacing, y, 80, 20, () -> new TranslatableText("No"), (button) -> {
			onDenied.deny();
			
			screen.closeWindow(this);
		});
		addContent(denyButton);
	}
	
	@Override
	protected void tickContents() {
		
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		for (int index = 0; index < message.size(); index++) {
			Text line = message.get(index);
			drawCenteredText(matrices, screen.getTextRenderer(), line, getX() + getWidth() / 2, getY() + 36 + index * 14, TEXT_COLOR);
		}
		confirmButton.render(matrices, mouseX, mouseY, delta);
		denyButton.render(matrices, mouseX, mouseY, delta);
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
	
	public interface ConfirmAction {
		void confirm();
	}
	
	public interface DenyAction {
		void deny();
	}
}

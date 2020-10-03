package redstonetweaks.gui.widget;

import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;

public class RTTextFieldWidget extends TextFieldWidget implements IAbstractButtonWidget {
	
	private final UpdateText updateText;
	
	private boolean allowHover = true;
	
	public RTTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, UpdateText updateText, Consumer<String> changedListener) {
		super(textRenderer, x + 1, y + 1, width - 2, height - 2, new TranslatableText(""));
		this.updateText = updateText;
		
		this.updateMessage();
		this.setChangedListener(changedListener);
	}
	
	public void unFocus() {
		setFocused(false);
		updateText.update(this);
	}
	
	@Override
	public boolean isHovered() {
		return allowHover && super.isHovered();
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setX(int x) {
		this.x = x + 1;
	}
	
	@Override
	public void setY(int y) {
		this.y = y + 1;
	}
	
	@Override
	public int getWidth() {
		return width + 2;
	}
	
	@Override
	public void updateMessage() {
		updateText.update(this);
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		this.allowHover = allowHover;
	}
	
	public interface UpdateText {
		void update(RTTextFieldWidget textField);
	}
}

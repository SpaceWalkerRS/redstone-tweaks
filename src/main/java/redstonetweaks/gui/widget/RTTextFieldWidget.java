package redstonetweaks.gui.widget;

import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class RTTextFieldWidget extends TextFieldWidget implements IAbstractButtonWidget {
	
	private final UpdateText updateText;
	
	private boolean allowHover = true;
	private boolean deaf = false;
	
	public RTTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, UpdateText updateText, Consumer<String> changedListener) {
		this(textRenderer, 32, x, y, width, height, updateText, changedListener);
	}
	
	public RTTextFieldWidget(TextRenderer textRenderer, int maxLength, int x, int y, int width, int height, UpdateText updateText, Consumer<String> changedListener) {
		super(textRenderer, x + 1, y + 1, width - 2, height - 2, new TranslatableText(""));
		this.updateText = updateText;
		
		this.setMaxLength(maxLength);
		this.updateText();
		this.setChangedListener((text) -> {
			if (!deaf) {
				changedListener.accept(text);
			}
		});
	}
	
	@Override
	public boolean isHovered() {
		return allowHover && super.isHovered();
	}
	
	@Override
	public void setSelected(boolean selected) {
		if (isFocused() && !selected) {
			updateText();
		}
		
		super.setSelected(selected);
	}
	
	@Override
	public int getX() {
		return x - 1;
	}
	
	@Override
	public int getY() {
		return y - 1;
	}
	
	@Override
	public int getWidth() {
		return width + 2;
	}
	
	@Override
	public int getHeight() {
		return height + 2;
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		this.allowHover = allowHover;
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
	public void updateMessage() {
		if (!isFocused()) {
			deaf = true;
			updateText();
			deaf = false;
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
	}
	
	@Override
	public void setActive(boolean active) {
		setEditable(active);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	public void unFocus() {
		if (isFocused()) {
			setFocused(false);
			deaf = true;
			updateText();
			deaf = false;
		}
	}
	
	private void updateText() {
		updateText.update(this);
	}
	
	public interface UpdateText {
		void update(RTTextFieldWidget textField);
	}
}

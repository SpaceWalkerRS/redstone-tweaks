package redstonetweaks.gui;

import java.util.function.Supplier;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class RTTextFieldWidget extends TextFieldWidget {
	
	private final Supplier<String> supplier;
	
	public RTTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Supplier<String> supplier) {
		super(textRenderer, x, y, width, height, text);
		this.supplier = supplier;
	}
	
	public void unFocus() {
		setFocused(false);
		setText(supplier.get());
	}
}

package redstonetweaks.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class RTTextFieldWidget extends TextFieldWidget {

	public RTTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
		super(textRenderer, x, y, width, height, text);
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		
		
	}
}

package redstonetweaks.gui;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;

public abstract class RTAbstractParentElement extends AbstractParentElement {
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean clicked = mouseClick(mouseX, mouseY, button);
		
		unfocusTextFields(clicked ? getFocused() : null);
		
		return clicked;
	}
	
	private boolean mouseClick(double mouseX, double mouseY, int button) {
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		}
		
		boolean clicked = false;
		for (Element el : children()) {
			if (el.mouseClicked(mouseX, mouseY, button)) {
				setFocused(el);
				clicked = true;
			}
		}
		
		if (button == 0) {
			setDragging(true);
		}
		
		return clicked;
	}
	
	public abstract void unfocusTextFields(Element except);
	
}

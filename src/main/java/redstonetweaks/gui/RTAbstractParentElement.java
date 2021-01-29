package redstonetweaks.gui;

import java.util.Iterator;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import redstonetweaks.gui.widget.RTTextFieldWidget;

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
		
		Iterator<? extends Element> it = children().iterator();
		while (it.hasNext()) {
			Element el = it.next();
			
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
	
	public void unfocusTextFields(Element except) {
		for (Element el : children()) {
			if (el != except && el instanceof RTTextFieldWidget) {
				((RTTextFieldWidget)el).unFocus();
			}
		}
	}
	
}

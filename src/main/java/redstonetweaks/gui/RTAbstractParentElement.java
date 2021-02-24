package redstonetweaks.gui;

import java.util.Iterator;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import redstonetweaks.gui.widget.RTTextFieldWidget;

public abstract class RTAbstractParentElement extends AbstractParentElement {
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isMouseOver(mouseX, mouseY)) {
			setFocused(null);
			
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
		
		if (!clicked) {
			setFocused(null);
		}
		
		if (button == 0) {
			setDragging(true);
		}
		
		return clicked || consumeClick();
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 && getFocused() instanceof RTTextFieldWidget) {
			setFocused(null);
			
			return true;
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void setFocused(Element e) {
		Element focused = getFocused();
		
		if (e == focused) {
			return;
		}
		
		if (focused != null && focused instanceof RTElement) {
			((RTElement)focused).unfocus();
		}
		
		super.setFocused(e);
		
		if (e != null && e instanceof RTElement) {
			((RTElement)e).focus();
		}
	}
	
	protected boolean consumeClick() {
		return false;
	}
}

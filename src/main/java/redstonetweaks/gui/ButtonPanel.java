package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.util.math.MatrixStack;

import redstonetweaks.gui.widget.IAbstractButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;

public class ButtonPanel implements ParentElement, RTElement {
	
	private static final int SPACING = 5;
	
	public final RTMenuScreen screen;
	private final List<IAbstractButtonWidget> buttons;
	private final List<Action> actions;
	
	private Element focused;
    private boolean dragging;
    
    private int x;
    private int y;
	
	public ButtonPanel(RTMenuScreen screen) {
		this.screen = screen;
		this.buttons = new ArrayList<>();
		this.actions = new ArrayList<>();
	}
	
	@Override
	public List<? extends Element> children() {
		return buttons;
	}
	
	@Override
	public boolean isDragging() {
		return dragging;
	}
	
	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}
	
	@Override
	public Element getFocused() {
		return focused;
	}
	
	@Override
	public void setFocused(Element focused) {
		this.focused = focused;
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		buttons.forEach((button) -> button.allowHover(allowHover));
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		int width = - SPACING;
		for (IAbstractButtonWidget button : buttons) {
			width += button.getWidth() + SPACING;
		}
		
		return width;
	}
	
	public int getHeight() {
		return buttons.get(0).getHeight();
	}
	
	public int getHeaderHeight() {
		return 0;
	}
	
	public void addButton(IAbstractButtonWidget button) {
		buttons.add(button);
	}
	
	public void addAction(Action action) {
		actions.add(action);
	}
	
	public void doActions() {
		actions.forEach((action) -> action.execute());
	}
	
	public void setX(int x) {
		this.x = x;
		updateButtonPositions();
	}
	
	private void updateButtonPositions() {
		buttons.forEach((button) -> button.setX(x));
	}
	
	public void updateButtonLabels() {
		buttons.forEach((button) -> button.updateMessage());
	}
	
	public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float tickDelta) {
		for (IAbstractButtonWidget button : buttons) {
			button.setY(y);
			button.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
	
	public void tick() {
		buttons.forEach((button) -> button.tick());
	}
	
	public void unfocusTextFields() {
		for (IAbstractButtonWidget button : buttons) {
			if (button instanceof RTTextFieldWidget) {
				((RTTextFieldWidget)button).unFocus();
			}
		}
	}
	
	public void setActive(boolean active) {
		buttons.forEach((button) -> button.setActive(active));
	}
	
	public interface Action {
		void execute();
	}
}

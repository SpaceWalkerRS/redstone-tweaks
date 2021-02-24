package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

import redstonetweaks.gui.widget.IAbstractButtonWidget;

public class ButtonPanel extends RTAbstractParentElement implements RTElement {
	
	private static final int DEFAULT_SPACING = 2;
	
	private final List<IAbstractButtonWidget> buttons;
	private final int spacing;
    
    private int x;
    private int y;
	
	public ButtonPanel() {
		this(DEFAULT_SPACING);
	}
	
	public ButtonPanel(int spacing) {
		this.buttons = new ArrayList<>();
		this.spacing = spacing;
		
		this.x = 0;
		this.y = 0;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + 20;
	}
	
	@Override
	public List<? extends Element> children() {
		return buttons;
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		buttons.forEach((button) -> button.allowHover(allowHover));
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		IAbstractButtonWidget first = buttons.get(0);
		IAbstractButtonWidget last = buttons.get(buttons.size() - 1);
		
		return last.getX() + last.getWidth() - first.getX();
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
	
	public void setX(int x) {
		this.x = x;
		updateButtonPositions();
	}
	
	public void setY(int y) {
		this.y = y;
		updateButtonPositions();
	}
	
	private void updateButtonPositions() {
		int currentX = getX();
		for (IAbstractButtonWidget button : buttons) {
			button.setX(currentX);
			button.setY(getY());
			
			currentX += button.getWidth() + spacing;
		}
	}
	
	public void updateButtonLabels() {
		buttons.forEach((button) -> button.updateMessage());
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		for (IAbstractButtonWidget button : buttons) {
			button.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
	
	public void tick() {
		buttons.forEach((button) -> button.tick());
	}
	
	public void setActive(boolean active) {
		buttons.forEach((button) -> button.setActive(active));
	}
	
	public void setVisible(boolean visible) {
		buttons.forEach((button) -> button.setVisible(visible));
	}
}

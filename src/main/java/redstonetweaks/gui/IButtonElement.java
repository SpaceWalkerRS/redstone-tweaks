package redstonetweaks.gui;

import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.util.math.MatrixStack;

public interface IButtonElement extends ParentElement {
	
	public void init(boolean centered, int titleWidth);
	
	public int getWidth();
	
	public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float tickDelta);
	
	public void tick();
	
	public void unfocusTextFields();
	
	public void updateButtonLabels();
	
	public void setActive(boolean active);
	
	public void addAction(Action action);
	
	public interface Action {
		void execute();
	}
}

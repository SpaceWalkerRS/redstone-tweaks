package redstonetweaks.gui.widget;

import net.minecraft.client.util.math.MatrixStack;

import redstonetweaks.gui.RTElement;

public interface IAbstractButtonWidget extends RTElement {
	
	public void setX(int x);
	
	public void setY(int y);
	
	public void updateMessage();
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
	
	public void setActive(boolean active);
	
}

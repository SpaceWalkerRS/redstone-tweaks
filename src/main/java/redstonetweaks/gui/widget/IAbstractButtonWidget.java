package redstonetweaks.gui.widget;

import net.minecraft.client.util.math.MatrixStack;

import redstonetweaks.gui.RTElement;

public interface IAbstractButtonWidget extends RTElement {
	
	public abstract void setX(int x);
	
	public abstract void setY(int y);
	
	public abstract void updateMessage();
	
	public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
	
	public abstract void setActive(boolean active);
	
}

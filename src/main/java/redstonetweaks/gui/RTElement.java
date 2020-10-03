package redstonetweaks.gui;

import net.minecraft.client.gui.Element;

public interface RTElement extends Element {
	
	public abstract int getX();
	
	public abstract int getY();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract void tick();
	
	public void allowHover(boolean allowHover);
	
}

package redstonetweaks.gui;

import net.minecraft.client.gui.Element;

public interface RTElement extends Element {
	
	public int getX();
	
	public int getY();
	
	public int getWidth();
	
	public int getHeight();
	
	public void tick();
	
	public void allowHover(boolean allowHover);
	
}

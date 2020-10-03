package redstonetweaks.gui;

public interface RTParentElement {
	
	public RTParentElement getParent();
	
	public int getX();
	
	public int getY();
	
	public int getWidth();
	
	public int getHeight();
	
	public int getHeaderHeight();
	
	default void openWindow(RTWindow window) {
		getParent().openWindow(window);
	}
	
	default void closeWindow(RTWindow window) {
		getParent().closeWindow(window);
	}
	
}

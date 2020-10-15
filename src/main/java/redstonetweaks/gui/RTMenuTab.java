package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import redstonetweaks.gui.widget.RTTextFieldWidget;

public abstract class RTMenuTab extends RTAbstractParentElement {
	
	public final RTMenuScreen screen;
	private final Text title;
	private final List<RTElement> contents;
	protected final List<RTWindow> windows;
	private final List<RTWindow> closedWindows;
	
	public RTMenuTab(RTMenuScreen screen, Text title) {
		this.screen = screen;
		this.title = title;
		this.contents = new ArrayList<>();
		
		this.windows = new ArrayList<>();
		this.closedWindows = new ArrayList<>();
	}
	
	@Override
	public List<? extends Element> children() {
		return windows.isEmpty() ? contents : windows;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= screen.getHeaderHeight();
	}
	
	public Text getTitle() {
		return title;
	}
	
	public void init() {
		contents.clear();
		setFocused(null);
		
		initContents();
		allowHover(windows.isEmpty());
	}

	protected abstract void initContents();
	
	public void tick() {
		if (!closedWindows.isEmpty()) {
			closedWindows.forEach((window) -> windows.remove(window));
			closedWindows.clear();
			
			if (windows.isEmpty()) {
				allowHover(true);
			}
		}
		
		tickContents();
		tickWindows();
	}

	protected abstract void tickContents();

	private void tickWindows() {
		Iterator<RTWindow> windows = this.windows.iterator();
		
		while (windows.hasNext()) {
			windows.next().tick();
		}
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderContents(matrices, mouseX, mouseY, delta);
		renderWindows(matrices, mouseX, mouseY, delta);
	}

	protected abstract void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta);

	private void renderWindows(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		windows.forEach((window) -> window.render(matrices, mouseX, mouseY, delta));
	}
	
	protected void addContent(RTElement content) {
		contents.add(content);
	}
	
	public void openWindow(RTWindow window) {
		if (windows.isEmpty() && window != null) {
			allowHover(false);
		}
		
		window.init();
		windows.add(window);
	}
	
	public void closeWindow(RTWindow window) {
		closedWindows.add(window);
	}
	
	public boolean closeTopWindow() {
		if (!windows.isEmpty()) {
			closeWindow(windows.get(0));
			return true;
		}
		return false;
	}
	
	private void allowHover(boolean allowHover) {
		contents.forEach((element) -> element.allowHover(allowHover));
	}
	
	public abstract void onTabClosed();
	
	public boolean focusedIsTextField() {
		if (getFocused() instanceof RTTextFieldWidget && ((RTTextFieldWidget)getFocused()).isActive()) {
			return true;
		}
		if (windows.isEmpty()) {
			return hasFocusedTextField();
		} else {
			for (RTWindow window : windows) {
				if (window.hasFocusedTextField()) {
					return true;
				}
			}
			return false;
		}
	}
	
	protected abstract boolean hasFocusedTextField();
	
}

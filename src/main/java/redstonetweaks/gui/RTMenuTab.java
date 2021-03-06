package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import redstonetweaks.gui.widget.RTTextFieldWidget;

public abstract class RTMenuTab extends RTAbstractParentElement {
	
	protected static final int TEXT_COLOR = 16777215;
	
	public final RTMenuScreen screen;
	private final Text title;
	private final List<RTElement> contents;
	private final List<RTWindow> windows;
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
		return windows.isEmpty() ? getContents() : getWindows();
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= screen.getHeaderHeight();
	}
	
	@Override
	protected boolean consumeClick() {
		return true;
	}
	
	public Text getTitle() {
		return title;
	}
	
	protected List<RTElement> getContents() {
		return contents;
	}
	
	protected List<RTWindow> getWindows() {
		return windows;
	}
	
	protected void clearContents() {
		contents.clear();
	}

	protected void addContent(RTElement content) {
		contents.add(content);
	}
	
	private void addWindow(RTWindow window) {
		windows.add(window);
	}
	
	private void removeWindow(RTWindow window) {
		windows.remove(window);
	}
	
	public void init() {
		clearContents();
		setFocused(null);
		
		initContents();
		allowHover(!hasWindowOpen());
	}
	
	protected abstract void initContents();
	
	public void refresh() {
		refreshContents();
		refreshWindows();
	}
	
	protected abstract void refreshContents();
	
	public void tick() {
		if (!closedWindows.isEmpty()) {
			closedWindows.forEach((window) -> removeWindow(window));
			closedWindows.clear();
			
			if (!hasWindowOpen()) {
				allowHover(true);
			}
		}
		
		tickContents();
		tickWindows();
	}

	protected abstract void tickContents();

	private void tickWindows() {
		windows.forEach((window) -> window.tick());
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderContents(matrices, mouseX, mouseY, delta);
		renderWindows(matrices, mouseX, mouseY, delta);
	}

	protected abstract void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta);

	private void renderWindows(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		windows.forEach((window) -> window.render(matrices, mouseX, mouseY, delta));
	}
	
	public void openWindow(RTWindow window) {
		if (!hasWindowOpen() && window != null) {
			allowHover(false);
		}
		
		window.init();
		addWindow(window);
	}
	
	public void closeWindow(RTWindow window) {
		closedWindows.add(window);
	}
	
	public boolean closeTopWindow() {
		if (hasWindowOpen()) {
			closeWindow(windows.get(0));
			
			return true;
		}
		
		return false;
	}
	
	protected void refreshWindows() {
		getWindows().forEach((window) -> window.refresh());
	}
	
	public boolean hasWindowOpen() {
		return !getWindows().isEmpty();
	}
	
	private void allowHover(boolean allowHover) {
		getContents().forEach((element) -> element.allowHover(allowHover));
	}
	
	public boolean canClose() {
		Element focused = getFocused();
		
		return focused == null || !(focused instanceof RTTextFieldWidget);
	}
	
	public abstract void onTabClosed();
	
}

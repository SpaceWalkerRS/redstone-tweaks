package redstonetweaks.gui.info;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;

public class InfoTab extends RTMenuTab {
	
	private InfoListWidget list;
	
	public InfoTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Info"));
	}
	
	@Override
	protected void initContents() {
		list = new InfoListWidget(screen);
		list.init();
		addContent(list);
	}
	
	@Override
	protected void refreshContents() {
		init();
	}
	
	@Override
	protected void tickContents() {
		list.tick();
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		list.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void onTabClosed() {
		list.saveScrollAmount();
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return list.focusedIsTextField();
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		
	}
}

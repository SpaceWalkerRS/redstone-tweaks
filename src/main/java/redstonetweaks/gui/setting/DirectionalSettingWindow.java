package redstonetweaks.gui.setting;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.setting.types.DirectionalSetting;
import redstonetweaks.setting.types.ISetting;

public class DirectionalSettingWindow extends RTWindow implements ISettingGUIElement {
	
	private static final int WIDTH = 230;
	private static final int HEIGHT = 185;
	
	private final DirectionalSetting<?> setting;
	private DirectionalSettingListWidget list;
	
	public DirectionalSettingWindow(RTMenuScreen screen, DirectionalSetting<?> setting) {
		super(screen, new TranslatableText(setting.getName()), (screen.getWidth() - WIDTH) / 2, (screen.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.setting = setting;
	}
	
	@Override
	protected void tickContents() {
		list.tick();
	}
	
	@Override
	protected void initContents() {
		list = new DirectionalSettingListWidget(screen, getX(), getY() + getHeaderHeight(), getWidth(), getHeight() - getHeaderHeight(), setting);
		addChild(list);
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		list.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void onSettingChanged(ISetting setting) {
		if (this.setting == setting) {
			list.onSettingChanged();
		}
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return list.focusedIsTextField();
	}
}

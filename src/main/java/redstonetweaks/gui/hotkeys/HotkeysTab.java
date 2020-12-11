package redstonetweaks.gui.hotkeys;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.hotkeys.Hotkeys;
import redstonetweaks.hotkeys.RTKeyBinding;
import redstonetweaks.interfaces.RTIMinecraftClient;

public class HotkeysTab extends RTMenuTab {
	
	private static final int HEADER_HEIGHT = 25;
	
	private final Hotkeys hotkeys;
	
	private HotkeysListWidget hotkeysList;
	private RTButtonWidget resetButton;
	
	public RTKeyBinding focusedKeyBinding;
	
	public HotkeysTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Hotkeys"));
		
		this.hotkeys = ((RTIMinecraftClient)screen.client).getHotkeysManager().getHotkeys();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (focusedKeyBinding == null) {
			return super.mouseClicked(mouseX, mouseY, button);
		}
		updateFocusedKeyBinding(InputUtil.Type.MOUSE.createFromCode(button));
		
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (focusedKeyBinding == null) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		if (keyCode == 256) {
			if (focusedKeyBinding.allowUnbinding()) {
				updateFocusedKeyBinding(InputUtil.UNKNOWN_KEY);
			} else {
				updateFocusedKeyBinding(focusedKeyBinding.getKey());
			}
		} else {
			updateFocusedKeyBinding(InputUtil.fromKeyCode(keyCode, scanCode));
		}
		
		return true;
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		hotkeysList.render(matrices, mouseX, mouseY, delta);
		resetButton.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	protected void initContents() {
		hotkeysList = new HotkeysListWidget(this, hotkeys, 0, screen.getHeaderHeight() + HEADER_HEIGHT, screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - 5);
		hotkeysList.init();
		addContent(hotkeysList);
		
		resetButton = new RTButtonWidget(5, screen.getHeaderHeight(), 50, 20, () -> new TranslatableText("RESET"), (button) -> {
			hotkeys.resetKeyBindings();
		});
		addContent(resetButton);
	}
	
	@Override
	protected void refreshContents() {
		hotkeysList.saveScrollAmount();
		hotkeysList.init();
	}
	
	@Override
	protected void tickContents() {
		
	}
	
	@Override
	public void onTabClosed() {
		hotkeysList.saveScrollAmount();
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return false;
	}
	
	private void updateFocusedKeyBinding(Key newKey) {
		RTKeyBinding keyBinding = focusedKeyBinding;
		focusedKeyBinding = null;
		
		hotkeys.updateKeyBinding(keyBinding, newKey);
	}
	
	public void onHotkeyChanged(RTKeyBinding keyBinding) {
		hotkeysList.onHotkeyChanged(keyBinding);
	}
}

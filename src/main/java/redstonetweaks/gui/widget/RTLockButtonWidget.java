package redstonetweaks.gui.widget;

import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public class RTLockButtonWidget extends LockButtonWidget implements IAbstractButtonWidget {
	
	private final PressAction onPress;
	
	private boolean alwaysActive = false;
	private boolean allowHover = true;
	
	public RTLockButtonWidget(int x, int y, PressAction onPress) {
		this(x, y, false, onPress);
	}
	
	public RTLockButtonWidget(int x, int y, boolean locked, PressAction onPress) {
		super(x, y, RTButtonWidget.NONE);
		this.setLocked(locked);
		
		this.onPress = onPress;
	}
	
	@Override
	public void onPress() {
		onPress.onPress(this);
	}
	
	@Override
	public boolean isHovered() {
		return allowHover && super.isHovered();
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		this.allowHover = allowHover;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public void updateMessage() {
		
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = alwaysActive || active;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public RTLockButtonWidget alwaysActive() {
		alwaysActive = true;
		return this;
	}
	
	public void toggleLocked() {
		setLocked(!isLocked());
	}
	
	public interface PressAction {
		void onPress(RTLockButtonWidget button);
	}
}

package redstonetweaks.gui.widget;

import java.util.function.Supplier;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class RTButtonWidget extends ButtonWidget implements IAbstractButtonWidget {
	
	private static final ButtonWidget.PressAction NONE = (button) -> {};
	
	private final PressAction onPress;
	private final Supplier<Text> messageSupplier;
	
	private boolean alwaysActive = false;
	private boolean allowHover = true;
	
	public RTButtonWidget(int x, int y, int width, int height, Supplier<Text> messageSupplier, PressAction onPress) {
		this(x, y, width, height, messageSupplier, onPress, EMPTY);
	}
	
	public RTButtonWidget(int x, int y, int width, int height, Supplier<Text> messageSupplier, PressAction onPress, TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, messageSupplier.get(), NONE);
		
		this.onPress = onPress;
		this.messageSupplier = messageSupplier;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button);
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
	public int getWidth() {
		return super.getWidth();
	}
	
	@Override
	public int getHeight() {
		return super.getHeight();
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
		setMessage(messageSupplier.get());
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
	
	public RTButtonWidget alwaysActive() {
		alwaysActive = true;
		return this;
	}
	
	public interface PressAction {
		void onPress(RTButtonWidget button);
	}
}

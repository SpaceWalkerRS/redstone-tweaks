package redstonetweaks.gui.widget;

import java.util.function.Supplier;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class RTSliderWidget extends SliderWidget implements IAbstractButtonWidget {
	
	private final Supplier<Text> messageSupplier;
	private final SlideAction onSlide;
	private final Snap snap;
	
	private boolean allowHover = true;
	
	public RTSliderWidget(int x, int y, int width, int height, Supplier<Text> messageSupplier, SlideAction onSlide, Snap snap) {
		this(x, y, width, height, 0.0D, messageSupplier, onSlide, snap);
	}
	
	public RTSliderWidget(int x, int y, int width, int height, double value, Supplier<Text> messageSupplier, SlideAction onSlide, Snap snap) {
		super(x, y, width, height, messageSupplier.get(), value);
		
		this.messageSupplier = messageSupplier;
		this.onSlide = onSlide;
		this.snap = snap;
		
		this.snap();
	}
	
	@Override
	public void updateMessage() {
		setMessage(messageSupplier.get());
		snap();
	}
	
	@Override
	protected void applyValue() {
		onSlide.onSlide(this);
		snap();
	}
	
	@Override
	public boolean isHovered() {
		return allowHover && active && super.isHovered();
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double newValue) {
		value = newValue;
	}
	
	public void snap() {
		snap.snap(this);
	}
	
	public interface SlideAction {
		void onSlide(RTSliderWidget slider);
	}
	
	public interface Snap {
		void snap(RTSliderWidget slider);
	}
}

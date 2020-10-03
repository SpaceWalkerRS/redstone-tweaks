package redstonetweaks.gui.widget;

import java.util.function.Supplier;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class RTSliderWidget extends SliderWidget implements IAbstractButtonWidget {
	
	private final Supplier<Text> messageSupplier;
	private final SlideAction onSlide;
	private final Snap snap;
	
	private boolean allowHover = true;
	
	public RTSliderWidget(int x, int y, int width, int height, double value, Supplier<Text> messageSupplier, SlideAction onSlide, Snap snap) {
		super(x, y, width, height, messageSupplier.get(), value);
		
		this.messageSupplier = messageSupplier;
		this.onSlide = onSlide;
		this.snap = snap;
		
		this.snap();
	}
	
	@Override
	public void updateMessage() {
		this.setMessage(messageSupplier.get());
	}
	
	@Override
	protected void applyValue() {
		onSlide.onSlide(this);
		snap();
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
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		this.allowHover = allowHover;
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

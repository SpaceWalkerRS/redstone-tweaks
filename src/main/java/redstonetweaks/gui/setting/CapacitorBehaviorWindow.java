package redstonetweaks.gui.setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.block.capacitor.CapacitorBehavior;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.setting.types.CapacitorBehaviorSetting;
import redstonetweaks.setting.types.ISetting;

public class CapacitorBehaviorWindow extends RTWindow {
	
	private static final int WIDTH = 232;
	private static final int HEIGHT = 128;
	
	private final CapacitorBehaviorSetting setting;
	private final Supplier<CapacitorBehavior> capacitorSupplier;
	private final Consumer<ISetting> changeListener;
	
	private CapacitorBehavior capacitor;
	private RTButtonWidget modeButton;
	private RTTextFieldWidget stepSizeField;
	private RTTextFieldWidget fadeInDelayField;
	private RTTextFieldWidget fadeOutDelayField;
	
	private boolean capacitorBehaviorChanged;
	private boolean canEdit;
	
	public CapacitorBehaviorWindow(RTMenuScreen screen, CapacitorBehaviorSetting setting, Supplier<CapacitorBehavior> capacitorSupplier, Consumer<ISetting> changeListener) {
		super(screen, new TranslatableText("Capacitor Behavior"), (screen.getWidth() - WIDTH) / 2, (screen.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.setting = setting;
		this.capacitorSupplier = capacitorSupplier;
		this.changeListener = (updateOrderSetting) -> {
			changeListener.accept(updateOrderSetting);
		};
		
		this.canEdit = true;
	}
	
	@Override
	protected void initContents() {
		capacitor = capacitorSupplier.get();
		
		int x = getX() + 100;
		int y = getY() + 34;
		int width = 120;
		
		modeButton = new RTButtonWidget(x, y, width, 20, () -> new TranslatableText(capacitor.getMode().toString()), (button) -> {
			capacitor.cycleMode(!Screen.hasShiftDown());
			
			capacitorBehaviorChanged = true;
			
			button.updateMessage();
		});
		addContent(modeButton);
		
		stepSizeField = new RTTextFieldWidget(screen.getTextRenderer(), x, y + 22, width, 20, (textField) -> {
			textField.setText(String.valueOf(capacitor.getStepSize()));
		}, (text) -> {
			try {
				int newStepSize = Integer.parseInt(text);
				if (newStepSize > 0 && newStepSize != capacitor.getStepSize()) {
					capacitor.setStepSize(newStepSize);
					
					capacitorBehaviorChanged = true;
				}
			} catch (Exception e) {
				
			}
		});
		addContent(stepSizeField);
		
		fadeInDelayField = new RTTextFieldWidget(screen.getTextRenderer(), x, y + 44, width, 20, (textField) -> {
			textField.setText(String.valueOf(capacitor.getIncrementDelay()));
		}, (text) -> {
			try {
				int newDelay = Integer.parseInt(text);
				if (newDelay >= 0 && newDelay != capacitor.getIncrementDelay()) {
					capacitor.setIncrementDelay(newDelay);
					
					capacitorBehaviorChanged = true;
				}
			} catch (Exception e) {
				
			}
		});
		addContent(fadeInDelayField);
		
		fadeOutDelayField = new RTTextFieldWidget(screen.getTextRenderer(), x, y + 66, width, 20, (textField) -> {
			textField.setText(String.valueOf(capacitor.getDecrementDelay()));
		}, (text) -> {
			try {
				int newDelay = Integer.parseInt(text);
				if (newDelay >= 0 && newDelay != capacitor.getDecrementDelay()) {
					capacitor.setDecrementDelay(newDelay);
					
					capacitorBehaviorChanged = true;
				}
			} catch (Exception e) {
				
			}
		});
		addContent(fadeOutDelayField);
		
		modeButton.setActive(canEdit);
		stepSizeField.setActive(canEdit);
		fadeInDelayField.setActive(canEdit);
		fadeOutDelayField.setActive(canEdit);
	}
	
	@Override
	protected void tickContents() {
		if (capacitorBehaviorChanged) {
			capacitorBehaviorChanged = false;
			
			changeListener.accept(setting);
			
			refresh();
		}
		
		stepSizeField.tick();
		fadeInDelayField.tick();
		fadeOutDelayField.tick();
	}

	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int x = getX() + 10;
		
		screen.getTextRenderer().drawWithShadow(matrices, "Mode", x, modeButton.getY() + 6, TEXT_COLOR);
		screen.getTextRenderer().drawWithShadow(matrices, "Step Size", x, stepSizeField.getY() + 6, TEXT_COLOR);
		screen.getTextRenderer().drawWithShadow(matrices, "Fade-In Delay", x, fadeInDelayField.getY() + 6, TEXT_COLOR);
		screen.getTextRenderer().drawWithShadow(matrices, "Fade-Out Delay", x, fadeOutDelayField.getY() + 6, TEXT_COLOR);
		
		modeButton.render(matrices, mouseX, mouseY, delta);
		stepSizeField.render(matrices, mouseX, mouseY, delta);
		fadeInDelayField.render(matrices, mouseX, mouseY, delta);
		fadeOutDelayField.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	protected void onRefresh() {
		
	}
	
	public void disableButtons() {
		canEdit = false;
		refresh();
	}
	
	public void enableButtons() {
		canEdit = true;
		refresh();
	}
}

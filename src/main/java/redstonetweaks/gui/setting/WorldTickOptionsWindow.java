package redstonetweaks.gui.setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.WorldTickOptionsSetting;
import redstonetweaks.world.common.WorldTickOptions;

public class WorldTickOptionsWindow extends RTWindow {
	
	private static final int WIDTH = 190;
	private static final int HEIGHT = 110;
	
	private final WorldTickOptionsSetting setting;
	private final Supplier<WorldTickOptions> worldTickOptionsSupplier;
	private final Consumer<ISetting> changeListener;
	
	private RTButtonWidget modeButton;
	private RTButtonWidget dimensionFilterButton;
	private RTTextFieldWidget intervalField;
	
	private WorldTickOptions worldTickOptions;
	
	private boolean worldTickOptionsChanged;
	private boolean canEdit;
	
	public WorldTickOptionsWindow(RTMenuScreen screen, WorldTickOptionsSetting setting, Supplier<WorldTickOptions> worldTickOptionsSupplier, Consumer<ISetting> changeListener) {
		super(screen, new TranslatableText("World Tick Options"), (screen.getWidth() - WIDTH) / 2, (screen.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.setting = setting;
		this.worldTickOptionsSupplier = worldTickOptionsSupplier;
		this.changeListener = (updateOrderSetting) -> {
			changeListener.accept(updateOrderSetting);
		};
		
		this.canEdit = true;
	}
	
	@Override
	protected void initContents() {
		worldTickOptions = worldTickOptionsSupplier.get();
		
		int x = getX() + 100;
		int y = getY() + 30;
		
		modeButton = new RTButtonWidget(x, y, 80, 20, () -> new TranslatableText(worldTickOptions.getMode().getName()), (button) -> {
			worldTickOptions.cycleMode();
			
			worldTickOptionsChanged = true;
			
			button.updateMessage();
		});
		modeButton.setActive(canEdit);
		addContent(modeButton);
		
		dimensionFilterButton = new RTButtonWidget(x, y + 22, 80, 20, () -> new TranslatableText(worldTickOptions.getDimensionFilter().getName()), (button) -> {
			worldTickOptions.cycleDimensionFilter();
			
			worldTickOptionsChanged = true;
			
			button.updateMessage();
		});
		dimensionFilterButton.setActive(canEdit);
		addContent(dimensionFilterButton);
		
		intervalField = new RTTextFieldWidget(screen.getTextRenderer(), x, y + 44, 80, 20, (textField) -> {
			textField.setText(String.valueOf(worldTickOptions.getInterval()));
		}, (text) -> {
			try {
				int newInterval = Integer.parseInt(text);
				
				if (newInterval != worldTickOptions.getInterval()) {
					worldTickOptions.setInterval(newInterval);
					
					worldTickOptionsChanged = true;
				}
			} catch (Exception e) {
				
			}
		});
		intervalField.setActive(canEdit);
		addContent(intervalField);
	}
	
	@Override
	protected void tickContents() {
		if (worldTickOptionsChanged) {
			worldTickOptionsChanged = false;
			
			changeListener.accept(setting);
			
			refresh();
		}
		
		intervalField.tick();
	}

	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int x = getX() + 10;
		
		screen.getTextRenderer().drawWithShadow(matrices, "Mode", x, modeButton.getY() + 5, TEXT_COLOR);
		screen.getTextRenderer().drawWithShadow(matrices, "Dimension Filter", x, dimensionFilterButton.getY() + 5, TEXT_COLOR);
		screen.getTextRenderer().drawWithShadow(matrices, "Interval", x, intervalField.getY() + 5, TEXT_COLOR);
		
		modeButton.render(matrices, mouseX, mouseY, delta);
		dimensionFilterButton.render(matrices, mouseX, mouseY, delta);
		intervalField.render(matrices, mouseX, mouseY, delta);
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

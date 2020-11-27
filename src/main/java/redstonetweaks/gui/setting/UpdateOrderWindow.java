package redstonetweaks.gui.setting;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderWindow extends RTWindow implements ISettingGUIElement {
	
	private static final int WIDTH = 360;
	private static final int HEIGHT = 230;
	
	private final SettingsCategory category;
	private final UpdateOrderSetting setting;
	
	private UpdateOrderListWidget list;
	private RTButtonWidget notifierOrderButton;
	private ButtonPanel offsetButtons;
	private RTButtonWidget addUpdateButton;
	
	public UpdateOrderWindow(RTMenuScreen screen, SettingsCategory category, UpdateOrderSetting setting) {
		super(screen, new TranslatableText("Update Order"), (screen.getWidth() - WIDTH) / 2, (screen.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.category = category;
		this.setting = setting;
	}
	
	@Override
	protected void initContents() {
		notifierOrderButton = new RTButtonWidget(getX() + 32, getY() + 30, 140, 20, () -> new TranslatableText("Notifier Order: " + setting.get().getNotifierOrder().getName()), (button) -> {
			setting.get().cycleNotifierOrder();
			
			boolean locationalOrder = setting.get().getNotifierOrder() == UpdateOrder.NotifierOrder.LOCATIONAL;
			offsetButtons.setVisible(locationalOrder);
			
			((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
		});
		addChild(notifierOrderButton);
		
		boolean locationalOrder = setting.get().getNotifierOrder() == UpdateOrder.NotifierOrder.LOCATIONAL;
		
		offsetButtons = new ButtonPanel(15);
		offsetButtons.addButton(new RTTextFieldWidget(screen.getTextRenderer(), 0, 0, 40, 20, (textField) -> {
			textField.setText(String.valueOf(setting.get().getOffsetX()));
		}, (text) -> {
			try {
				int newOffset = Integer.parseInt(text);
				setting.get().setOffsetX(newOffset);
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			} catch (Exception e) {
				
			}
		}));
		offsetButtons.addButton(new RTTextFieldWidget(screen.getTextRenderer(), 0, 0, 40, 20, (textField) -> {
			textField.setText(String.valueOf(setting.get().getOffsetY()));
		}, (text) -> {
			try {
				int newOffset = Integer.parseInt(text);
				if (Math.abs(newOffset) < 256) {
					setting.get().setOffsetY(newOffset);
					
					((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
				}
			} catch (Exception e) {
				
			}
		}));
		offsetButtons.addButton(new RTTextFieldWidget(screen.getTextRenderer(), 0, 0, 40, 20, (textField) -> {
			textField.setText(String.valueOf(setting.get().getOffsetZ()));
		}, (text) -> {
			try {
				int newOffset = Integer.parseInt(text);
				setting.get().setOffsetZ(newOffset);
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			} catch (Exception e) {
				
			}
		}));
		offsetButtons.setX(getX() + getWidth() / 2 + 13);
		offsetButtons.setY(notifierOrderButton.getY());
		offsetButtons.setVisible(locationalOrder);
		addChild(offsetButtons);
		
		setHeaderHeight(55);
		
		addUpdateButton = new RTButtonWidget(getX() + (getWidth() - 100) / 2, getY() + getHeaderHeight() + 4, 100, 20, () -> new TranslatableText("Add Update"), (button) -> {
			setting.get().add(RelativePos.SELF, RelativePos.WEST);
			button.visible = false;
			
			((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
		});
		addUpdateButton.visible = false;
		addChild(addUpdateButton);
		
		updateButtonsActive();
		
		list = new UpdateOrderListWidget(screen, getX() + 2, getY() + getHeaderHeight(), getWidth() - 4, getHeight() - getHeaderHeight() - 18, category, setting);
		list.init();
		addChild(list);
	}
	
	@Override
	protected void tickContents() {
		if (list.children().isEmpty()) {
			addUpdateButton.setVisible(true);
		}
		
		offsetButtons.tick();
		list.tick();
	}

	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		notifierOrderButton.render(matrices, mouseX, mouseY, delta);
		offsetButtons.render(matrices, mouseX, mouseY, delta);
		addUpdateButton.render(matrices, mouseX, mouseY, delta);
		list.render(matrices, mouseX, mouseY, delta);
		
		if (setting.get().getNotifierOrder() == UpdateOrder.NotifierOrder.LOCATIONAL) {
			screen.client.textRenderer.drawWithShadow(matrices, new TranslatableText("X:"), offsetButtons.getX() - 10, offsetButtons.getY() + 6, TEXT_COLOR);
			screen.client.textRenderer.drawWithShadow(matrices, new TranslatableText("Y:"), offsetButtons.getX() + 45, offsetButtons.getY() + 6, TEXT_COLOR);
			screen.client.textRenderer.drawWithShadow(matrices, new TranslatableText("Z:"), offsetButtons.getX() + 100, offsetButtons.getY() + 6, TEXT_COLOR);
		}
		
		drawBackgroundTextureBelow(matrices, list.getY() + list.getHeight() + 5, mouseX, mouseY, delta);
	}

	@Override
	public void unfocusTextFields(Element except) {
		
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return offsetButtons.focusedIsTextField();
	}
	
	@Override
	public void onSettingChanged(ISetting setting) {
		if (this.setting == setting) {
			notifierOrderButton.updateMessage();
			offsetButtons.updateButtonLabels();
			updateButtonsActive();
			
			list.init();
		}
	}
	
	private void updateButtonsActive() {
		boolean canChangeSettings = ((RTIMinecraftClient)screen.client).getSettingsManager().canChangeSettings();
		
		notifierOrderButton.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
		offsetButtons.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
		addUpdateButton.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
	}
}

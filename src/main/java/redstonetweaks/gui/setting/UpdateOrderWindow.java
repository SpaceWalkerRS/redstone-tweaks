package redstonetweaks.gui.setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderWindow extends RTWindow {
	
	private static final int WIDTH = 360;
	private static final int HEIGHT = 230;
	
	private final UpdateOrderSetting setting;
	private final Supplier<UpdateOrder> updateOrderSupplier;
	private final Consumer<ISetting> changeListener;
	
	private UpdateOrder updateOrder;
	private UpdateOrderListWidget list;
	private RTButtonWidget notifierOrderButton;
	private ButtonPanel offsetButtons;
	private RTButtonWidget addUpdateButton;
	
	private boolean updateOrderChanged;
	private boolean canEdit;
	
	public UpdateOrderWindow(RTMenuScreen screen, UpdateOrderSetting setting, Supplier<UpdateOrder> updateOrderSupplier, Consumer<ISetting> changeListener) {
		super(screen, new TranslatableText("Update Order"), (screen.getWidth() - WIDTH) / 2, (screen.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.setting = setting;
		this.updateOrderSupplier = updateOrderSupplier;
		this.changeListener = (updateOrderSetting) -> {
			updateOrderChanged = true;
			changeListener.accept(updateOrderSetting);
		};
		
		this.canEdit = true;
	}
	
	@Override
	protected void initContents() {
		updateOrder = updateOrderSupplier.get();
		
		notifierOrderButton = new RTButtonWidget(getX() + 32, getY() + 30, 140, 20, () -> new TranslatableText("Notifier Order: " + updateOrder.getNotifierOrder().getName()), (button) -> {
			updateOrder.cycleNotifierOrder();
			
			boolean locationalOrder = updateOrder.getNotifierOrder() == UpdateOrder.NotifierOrder.LOCATIONAL;
			offsetButtons.setVisible(locationalOrder);
			
			changeListener.accept(setting);
			button.updateMessage();
		});
		notifierOrderButton.setActive(canEdit);
		addContent(notifierOrderButton);
		
		boolean locationalOrder = updateOrder.getNotifierOrder() == UpdateOrder.NotifierOrder.LOCATIONAL;
		
		offsetButtons = new ButtonPanel(15);
		offsetButtons.addButton(new RTTextFieldWidget(screen.getTextRenderer(), 0, 0, 40, 20, (textField) -> {
			textField.setText(String.valueOf(updateOrder.getOffsetX()));
		}, (text) -> {
			try {
				int newOffset = Integer.parseInt(text);
				if (updateOrder.getOffsetX() != newOffset) {
					updateOrder.setOffsetX(newOffset);
					
					changeListener.accept(setting);
				}
			} catch (Exception e) {
				
			}
		}));
		offsetButtons.addButton(new RTTextFieldWidget(screen.getTextRenderer(), 0, 0, 40, 20, (textField) -> {
			textField.setText(String.valueOf(updateOrder.getOffsetY()));
		}, (text) -> {
			try {
				int newOffset = Integer.parseInt(text);
				if (Math.abs(newOffset) < 256) {
					if (updateOrder.getOffsetY() != newOffset) {
						updateOrder.setOffsetY(newOffset);
						
						changeListener.accept(setting);
					}
				}
			} catch (Exception e) {
				
			}
		}));
		offsetButtons.addButton(new RTTextFieldWidget(screen.getTextRenderer(), 0, 0, 40, 20, (textField) -> {
			textField.setText(String.valueOf(updateOrder.getOffsetZ()));
		}, (text) -> {
			try {
				int newOffset = Integer.parseInt(text);
				if (updateOrder.getOffsetZ() != newOffset) {
					updateOrder.setOffsetZ(newOffset);
					
					changeListener.accept(setting);
				}
			} catch (Exception e) {
				
			}
		}));
		offsetButtons.setX(getX() + getWidth() / 2 + 13);
		offsetButtons.setY(notifierOrderButton.getY());
		offsetButtons.setVisible(locationalOrder);
		offsetButtons.setActive(canEdit);
		addContent(offsetButtons);
		
		setHeaderHeight(55);
		
		addUpdateButton = new RTButtonWidget(getX() + (getWidth() - 100) / 2, getY() + getHeaderHeight() + 4, 100, 20, () -> new TranslatableText("Add Update"), (button) -> {
			updateOrder.add(RelativePos.SELF, RelativePos.WEST);
			button.visible = false;
			
			changeListener.accept(setting);
		});
		addUpdateButton.setVisible(false);
		addUpdateButton.setActive(canEdit);
		addContent(addUpdateButton);
		
		list = new UpdateOrderListWidget(screen, getX() + 2, getY() + getHeaderHeight(), getWidth() - 4, getHeight() - getHeaderHeight() - 18, setting, updateOrder, changeListener);
		list.init();
		if (!canEdit) {
			list.disableButtons();
		}
		addContent(list);
	}
	
	@Override
	protected void tickContents() {
		if (updateOrderChanged) {
			updateOrderChanged = false;
			refresh();
		}
		
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
		
		if (updateOrder.getNotifierOrder() == UpdateOrder.NotifierOrder.LOCATIONAL) {
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
	protected void onRefresh() {
		list.saveScrollAmount();
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

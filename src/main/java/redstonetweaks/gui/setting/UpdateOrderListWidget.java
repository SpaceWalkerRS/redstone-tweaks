package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.AbstractNeighborUpdate;
import redstonetweaks.world.common.UpdateOrder;

public class UpdateOrderListWidget extends RTListWidget<UpdateOrderListWidget.Entry> {
	
	private final UpdateOrderSetting setting;
	private final UpdateOrder updateOrder;
	private final Consumer<ISetting> changeListener;
	
	public UpdateOrderListWidget(RTMenuScreen screen, int x, int y, int width, int height, UpdateOrderSetting setting, UpdateOrder updateOrder, Consumer<ISetting> changeListener) {
		super(screen, x, y, width, height, 22, setting.getId());
		
		this.setting = setting;
		this.updateOrder = updateOrder;
		this.changeListener = changeListener;
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	protected void initList() {
		int length = updateOrder.getNeighborUpdates().size();
		for (int i = 0; i < length; i++) {
			addEntry(new Entry(i));
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		
	}
	
	public void disableButtons() {
		for (Entry e : children()) {
			e.disableButtons();
		}
	}
	
	public class Entry extends RTListWidget.Entry<UpdateOrderListWidget.Entry> {
		
		private final AbstractNeighborUpdate update;
		private final List<RTElement> children;
		private final RTButtonWidget modeButton;
		private final ButtonPanel buttonPanel1;
		private final ButtonPanel buttonPanel2;
		
		public Entry(int index) {
			this.update = updateOrder.getNeighborUpdates().get(index);
			this.children = new ArrayList<>();
			
			this.modeButton = new RTButtonWidget(0, 0, 140, 20, () -> new TranslatableText("Mode: " + update.getMode().getName()), (button) -> {
				update.setMode(update.getMode().next());
				
				changeListener.accept(setting);
				button.updateMessage();
			});
			this.modeButton.setActive(!updateOrder.forceDefaultMode());
			this.children.add(modeButton);
			
			this.buttonPanel1 = new ButtonPanel();
			this.buttonPanel1.addButton(new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText(update.getNotifierPos().getName()), (button) -> {
				if (Screen.hasShiftDown()) {
					update.setNotifierPos(update.getNotifierPos().previous(updateOrder.getDirectionality()));
				} else {
					update.setNotifierPos(update.getNotifierPos().next(updateOrder.getDirectionality()));
				}
				
				changeListener.accept(setting);
				button.updateMessage();
			}));
			if (update.getMode() != AbstractNeighborUpdate.Mode.NEIGHBORS) {
				this.buttonPanel1.addButton(new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText(update.getUpdatePos().getName()), (button) -> {
					if (Screen.hasShiftDown()) {
						update.setUpdatePos(update.getUpdatePos().previous(updateOrder.getDirectionality()));
					} else {
						do {
							update.setUpdatePos(update.getUpdatePos().next(updateOrder.getDirectionality()));
						} while (setting == Tweaks.Global.SHAPE_UPDATE_ORDER && update.getUpdatePos() == RelativePos.SELF);
					}
					
					changeListener.accept(setting);
					button.updateMessage();
				}));
			}
			this.children.add(buttonPanel1);
			
			this.buttonPanel2 = new ButtonPanel();
			this.buttonPanel2.addButton(new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("+"), (button) -> {
				if (Screen.hasShiftDown()) {
					updateOrder.moveUp(index);
				} else
				if (Screen.hasControlDown()) {
					updateOrder.insert(index, update.copy());
				} else {
					updateOrder.insert(index + 1, RelativePos.SELF, RelativePos.WEST);
				}
				
				changeListener.accept(setting);
				button.updateMessage();
			}));
			this.buttonPanel2.addButton(new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("-"), (button) -> {
				if (Screen.hasShiftDown()) {
					updateOrder.moveDown(index);
				} else {
					updateOrder.remove(index);
				}
				
				changeListener.accept(setting);
				button.updateMessage();
			}));
			this.children.add(buttonPanel2);
			
			this.modeButton.setX(getX() + 30);
			this.buttonPanel1.setX(this.modeButton.getX() + this.modeButton.getWidth() + 2);
			this.buttonPanel2.setX(getX() + getWidth() - 57);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}

		@Override
		public void tick() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.drawWithShadow(matrices, new TranslatableText(index + "."), x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			modeButton.setY(y);
			modeButton.render(matrices, mouseX, mouseY, tickDelta);
			
			buttonPanel1.setY(y);
			buttonPanel1.render(matrices, mouseX, mouseY, tickDelta);
			
			buttonPanel2.setY(y);
			buttonPanel2.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
		
		private void disableButtons() {
			modeButton.setActive(false);
			buttonPanel1.setActive(false);
			buttonPanel2.setActive(false);
		}
	}
}

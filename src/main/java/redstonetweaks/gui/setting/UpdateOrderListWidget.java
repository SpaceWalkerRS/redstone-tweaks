package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.AbstractNeighborUpdate;

public class UpdateOrderListWidget extends RTListWidget<UpdateOrderListWidget.Entry> {
	
	private final SettingsCategory category;
	private final UpdateOrderSetting setting;
	
	private boolean updateOrderChanged;
	
	public UpdateOrderListWidget(RTMenuScreen screen, int x, int y, int width, int height, SettingsCategory category, UpdateOrderSetting setting) {
		super(screen, x, y, width, height, 22, setting.getId());
		
		this.category = category;
		this.setting = setting;
		
		init();
	}
	
	@Override
	public void tick() {
		if (updateOrderChanged) {
			init();
			updateOrderChanged = false;
		}
	}
	
	@Override
	protected void initList() {
		int length = setting.get().getNeighborUpdates().size();
		for (int i = 0; i < length; i++) {
			addEntry(new Entry(i));
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		
	}
	
	public class Entry extends RTListWidget.Entry<UpdateOrderListWidget.Entry> {
		
		private final AbstractNeighborUpdate update;
		private final List<RTElement> children;
		private final RTButtonWidget modeButton;
		private final ButtonPanel buttonPanel1;
		private final ButtonPanel buttonPanel2;
		
		public Entry(int index) {
			this.update = setting.get().getNeighborUpdates().get(index);
			this.children = new ArrayList<>();
			
			this.modeButton = new RTButtonWidget(0, 0, 140, 20, () -> new TranslatableText("Mode: " + update.getMode().getName()), (button) -> {
				update.setMode(update.getMode().next());
				updateOrderChanged = true;
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			});
			this.children.add(modeButton);
			
			this.buttonPanel1 = new ButtonPanel();
			this.buttonPanel1.addButton(new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText(update.getNotifierPos().getName()), (button) -> {
				if (Screen.hasShiftDown()) {
					update.setNotifierPos(update.getNotifierPos().previous(setting.get().getDirectionality()));
				} else {
					update.setNotifierPos(update.getNotifierPos().next(setting.get().getDirectionality()));
				}
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			}));
			if (update.getMode() != AbstractNeighborUpdate.Mode.NEIGHBORS) {
				this.buttonPanel1.addButton(new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText(update.getUpdatePos().getName()), (button) -> {
					if (Screen.hasShiftDown()) {
						update.setUpdatePos(update.getUpdatePos().previous(setting.get().getDirectionality()));
					} else {
						do {
							update.setUpdatePos(update.getUpdatePos().next(setting.get().getDirectionality()));
						} while (setting == Tweaks.Global.SHAPE_UPDATE_ORDER && update.getUpdatePos() == RelativePos.SELF);
					}
					
					((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
				}));
			}
			this.children.add(buttonPanel1);
			
			this.buttonPanel2 = new ButtonPanel();
			this.buttonPanel2.addButton(new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("+"), (button) -> {
				if (Screen.hasShiftDown()) {
					setting.get().moveUp(index);
				} else
				if (Screen.hasControlDown()) {
					setting.get().insert(index, update.copy());
				} else {
					setting.get().insert(index + 1, RelativePos.SELF, RelativePos.WEST);
				}
				updateOrderChanged = true;
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			}));
			this.buttonPanel2.addButton(new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("-"), (button) -> {
				if (Screen.hasShiftDown()) {
					setting.get().moveDown(index);
				} else {
					setting.get().remove(index);
				}
				updateOrderChanged = true;
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			}));
			this.children.add(buttonPanel2);
			
			this.modeButton.setX(getX() + 30);
			this.buttonPanel1.setX(this.modeButton.getX() + this.modeButton.getWidth() + 2);
			this.buttonPanel2.setX(getX() + getWidth() - 57);
			
			updateButtonsActive();
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}

		@Override
		public void tick() {
			
		}
		
		@Override
		public void unfocusTextFields() {
			
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
		
		private void updateButtonsActive() {
			boolean canChangeSettings = ((RTIMinecraftClient)screen.client).getSettingsManager().canChangeSettings();
			
			modeButton.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked() && !setting.get().modeLocked());
			buttonPanel1.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
			buttonPanel2.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
		}
	}
}

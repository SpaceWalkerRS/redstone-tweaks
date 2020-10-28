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
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.BlockUpdate;

public class UpdateOrderListWidget extends RTListWidget<UpdateOrderListWidget.Entry> {
	
	private final UpdateOrderSetting setting;
	
	private boolean updateCountChanged;
	
	public UpdateOrderListWidget(RTMenuScreen screen, int x, int y, int width, int height, UpdateOrderSetting setting) {
		super(screen, x, y, width, height, 22);
		
		this.setting = setting;
		init();
	}
	
	@Override
	public void tick() {
		if (updateCountChanged) {
			init();
			updateCountChanged = false;
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		
	}
	
	public void init() {
		children().clear();
		
		int length = setting.get().getBlockUpdates().size();
		for (int i = 0; i < length; i++) {
			addEntry(new Entry(i));
		}
	}
	
	public class Entry extends RTListWidget.Entry<UpdateOrderListWidget.Entry> {
		
		private final BlockUpdate update;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel1;
		private final ButtonPanel buttonPanel2;
		private final boolean buttonsActive;
		
		public Entry(int index) {
			this.buttonsActive = ((RTIMinecraftClient)client).getSettingsManager().canChangeSettings();
			
			this.update = setting.get().getBlockUpdates().get(index);
			this.children = new ArrayList<>();
			
			this.buttonPanel1 = new ButtonPanel(5);
			this.buttonPanel2 = new ButtonPanel(5);
			
			RTButtonWidget modeButton = new RTButtonWidget(0, 0, 140, 20, () -> new TranslatableText("Mode: " + update.getMode().getName()), (button) -> {
				update.setMode(update.getMode().next());
				updateCountChanged = true;
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			});
			this.buttonPanel1.addButton(modeButton);
			this.buttonPanel1.addButton(new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText(update.getNotifierPos().getName()), (button) -> {
				update.setNotifierPos(update.getNotifierPos().next(setting.get().getDirectionality()));
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			}));
			if (update.getMode() != BlockUpdate.Mode.NEIGHBORS) {
				this.buttonPanel1.addButton(new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText(update.getUpdatePos().getName()), (button) -> {
					update.setUpdatePos(update.getUpdatePos().next(setting.get().getDirectionality()));
					
					((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
				}));
			}
			this.buttonPanel1.setX(getX() + 30);
			this.buttonPanel1.setActive(buttonsActive);
			modeButton.setActive(buttonsActive && !setting.get().modeLocked());
			this.children.add(buttonPanel1);
			
			this.buttonPanel2.addButton(new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("+"), (button) -> {
				if (Screen.hasShiftDown()) {
					setting.get().insert(index, update.copy());
				} else {
					setting.get().insert(index + 1, RelativePos.SELF, RelativePos.WEST);
				}
				updateCountChanged = true;
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			}));
			this.buttonPanel2.addButton(new RTButtonWidget(0, 0, 20, 20, () -> new TranslatableText("-"), (button) -> {
				setting.get().remove(index);
				updateCountChanged = true;
				
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			}));
			this.buttonPanel2.setX(getX() + getWidth() - 60);
			this.buttonPanel2.setActive(buttonsActive);
			this.children.add(buttonPanel2);
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
			
			buttonPanel1.setY(y);
			buttonPanel1.render(matrices, mouseX, mouseY, tickDelta);
			buttonPanel2.setY(y);
			buttonPanel2.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
	}
}

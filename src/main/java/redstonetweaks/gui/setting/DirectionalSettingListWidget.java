package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.setting.types.DirectionalBooleanSetting;
import redstonetweaks.setting.types.DirectionalSetting;

public class DirectionalSettingListWidget extends RTListWidget<DirectionalSettingListWidget.Entry> {
	
	private final DirectionalSetting<?> setting;
	
	public DirectionalSettingListWidget(RTMenuScreen screen, int x, int y, int width, int height, DirectionalSetting<?> setting) {
		super(screen, x, y, width, height, 22);
		
		this.setting = setting;
		
		for (Direction dir : Direction.values()) {
			addEntry(new Entry(dir));
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		
	}
	
	public void onSettingChanged() {
		for (Entry entry : children()) {
			entry.onSettingChanged();
		}
	}
	
	public class Entry extends RTListWidget.Entry<DirectionalSettingListWidget.Entry> {
		
		private static final int TITLE_WIDTH = 75;
		
		private final Direction direction;
		private final Text title;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		private final RTButtonWidget resetButton;
		private final boolean buttonsActive;
		
		public Entry(Direction direction) {
			this.buttonsActive = ((MinecraftClientHelper)client).getSettingsManager().canChangeSettings();
			
			this.direction = direction;
			this.title = new TranslatableText(direction.getName());
			this.children = new ArrayList<>();
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanel();
			this.buttonPanel.setX(getX() + TITLE_WIDTH);
			this.buttonPanel.setActive(buttonsActive);
			this.children.add(buttonPanel);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (resetButton) -> {
				setting.reset();
				((MinecraftClientHelper)screen.client).getSettingsManager().onSettingChanged(setting);
			});
			this.resetButton.setX(this.buttonPanel.getX() + this.buttonPanel.getWidth() + 5);
			this.resetButton.setActive(buttonsActive && !setting.isDefault(direction));
			this.children.add(resetButton);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void allowHover(boolean allowHover) {
			children.forEach((element) -> element.allowHover(allowHover));
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.drawWithShadow(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			buttonPanel.setY(y);
			buttonPanel.render(matrices, mouseX, mouseY, tickDelta);
			
			resetButton.setY(y);
			resetButton.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		public void tick() {
			buttonPanel.tick();
		}
		
		@Override
		public void unfocusTextFields() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return buttonPanel.focusedIsTextField();
		}
		
		private void populateButtonPanel() {
			if (setting instanceof DirectionalBooleanSetting) {
				DirectionalBooleanSetting bSetting = (DirectionalBooleanSetting)setting;
				buttonPanel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> {
					Formatting formatting = bSetting.get(direction) ? Formatting.GREEN : Formatting.RED;
					return new TranslatableText(bSetting.valueToText(bSetting.get(direction))).formatted(formatting);
				}, (button) -> {
					bSetting.set(direction, !bSetting.get(direction));
					((MinecraftClientHelper)client).getSettingsManager().onSettingChanged(bSetting);
				}));
			}
		}
		
		private void onSettingChanged() {
			buttonPanel.updateButtonLabels();
			resetButton.setActive(buttonsActive && !setting.isDefault(direction));
		}
	}
}

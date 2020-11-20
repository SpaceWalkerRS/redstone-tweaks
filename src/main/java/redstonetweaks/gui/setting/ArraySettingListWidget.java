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
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.types.ArraySetting;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

public class ArraySettingListWidget extends RTListWidget<ArraySettingListWidget.Entry> {
	
	private final SettingsCategory category;
	private final ArraySetting<?, ?> setting;
	
	public ArraySettingListWidget(RTMenuScreen screen, int x, int y, int width, int height, SettingsCategory category, ArraySetting<?, ?> setting) {
		super(screen, x, y, width, height, 22);
		
		this.category = category;
		this.setting = setting;
		
		for (int index = 0; index < setting.getSize(); index++) {
			addEntry(new Entry(index));
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
	
	public class Entry extends RTListWidget.Entry<ArraySettingListWidget.Entry> {
		
		private static final int TITLE_WIDTH = 75;
		
		private final int index;
		private final Text title;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		private final RTButtonWidget resetButton;
		
		public Entry(int index) {
			this.index = index;
			this.title = new TranslatableText(setting.getKeyAsString(index));
			this.children = new ArrayList<>();
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanel();
			this.children.add(buttonPanel);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (resetButton) -> {
				setting.reset();
				((RTIMinecraftClient)screen.client).getSettingsManager().onSettingChanged(setting);
			});
			this.children.add(resetButton);
			
			this.buttonPanel.setX(getX() + TITLE_WIDTH);
			this.resetButton.setX(this.buttonPanel.getX() + this.buttonPanel.getWidth() + 5);
			
			updateButtonsActive();
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
			if (setting instanceof DirectionToBooleanSetting) {
				DirectionToBooleanSetting bSetting = (DirectionToBooleanSetting)setting;
				Direction dir = Direction.byId(index);
				buttonPanel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> {
					Formatting formatting = bSetting.get(dir) ? Formatting.GREEN : Formatting.RED;
					return new TranslatableText(bSetting.elementToString(bSetting.get(dir))).formatted(formatting);
				}, (button) -> {
					bSetting.set(dir, !bSetting.get(dir));
					((RTIMinecraftClient)client).getSettingsManager().onSettingChanged(bSetting);
				}));
			}
		}
		
		private void onSettingChanged() {
			buttonPanel.updateButtonLabels();
			updateButtonsActive();
		}
		
		private void updateButtonsActive() {
			boolean canChangeSettings = ((RTIMinecraftClient)screen.client).getSettingsManager().canChangeSettings();
			
			buttonPanel.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked());
			resetButton.setActive(canChangeSettings && !category.isLocked() && !setting.isLocked() && !setting.isDefault(index));
		}
	}
}

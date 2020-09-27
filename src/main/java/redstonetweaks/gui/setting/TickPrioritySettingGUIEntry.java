package redstonetweaks.gui.setting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.TickPriority;
import redstonetweaks.gui.SettingsListWidget;
import redstonetweaks.settings.types.TickPrioritySetting;

public class TickPrioritySettingGUIEntry extends SettingsListWidget.SettingEntry {
	
	private final TickPrioritySliderWidget editButton;
	
	public TickPrioritySettingGUIEntry(MinecraftClient client, TickPrioritySetting setting) {
		super(client, setting);
		
		editButton = new TickPrioritySliderWidget(0, 0, BUTTONS_WIDTH, BUTTONS_HEIGHT, setting);
		editButton.active = buttonsActive;
		buttons.add(editButton);
	}
	
	@Override
	public void renderButtons(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		editButton.x = x + titleWidth;
		editButton.y = y;
		editButton.render(matrices, mouseX, mouseY, tickDelta);
	}
	
	@Override
	public void updateButtonLabels() {
		editButton.update();
	}
	
	private class TickPrioritySliderWidget extends SliderWidget {
		
		private final TickPrioritySetting setting;
		
		public TickPrioritySliderWidget(int x, int y, int width, int height, TickPrioritySetting setting) {
			super(x, y, width, height, new TranslatableText(setting.getAsText()), 0.0D);
			this.setting = setting;
			
			update();
		}
		
		@Override
		protected void updateMessage() {
			this.setMessage(new TranslatableText(setting.getAsText()));
		}
		
		@Override
		protected void applyValue() {
			TickPriority[] priorities = TickPriority.values();
			
			int min = priorities[0].getIndex();
			int steps = (int)Math.round((priorities.length - 1) * this.value);
			
			setting.set(TickPriority.byIndex(min + steps));
			
			onSettingChanged();
		}
		
		public void update() {
			value = getSliderValue(setting.get());
		}
		
		private double getSliderValue(TickPriority priority) {
			TickPriority[] priorities = TickPriority.values();
			double steps = priority.getIndex() - priorities[0].getIndex();
			return steps / (priorities.length - 1);
		}
	}
}

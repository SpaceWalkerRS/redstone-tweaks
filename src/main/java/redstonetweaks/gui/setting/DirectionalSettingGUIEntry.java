package redstonetweaks.gui.setting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import redstonetweaks.gui.SettingsListWidget;
import redstonetweaks.settings.types.ISetting;

public class DirectionalSettingGUIEntry extends SettingsListWidget.SettingEntry {
	
	private final ButtonWidget editButton;
	
	public DirectionalSettingGUIEntry(MinecraftClient client, ISetting setting) {
		super(client, setting);
		
		editButton = new ButtonWidget(0, 0, BUTTONS_WIDTH, BUTTONS_HEIGHT, new TranslatableText("EDIT"), (button) -> {
			
		});
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
		
	}
}

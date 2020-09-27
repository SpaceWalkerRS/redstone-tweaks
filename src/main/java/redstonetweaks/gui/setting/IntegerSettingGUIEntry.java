package redstonetweaks.gui.setting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import redstonetweaks.gui.RTTextFieldWidget;
import redstonetweaks.gui.SettingsListWidget.SettingEntry;
import redstonetweaks.settings.types.IntegerSetting;

public class IntegerSettingGUIEntry extends SettingEntry {
	
	private final TextFieldWidget editButton;
	
	public IntegerSettingGUIEntry(MinecraftClient client, IntegerSetting setting) {
		super(client, setting);
		
		editButton = new RTTextFieldWidget(client.textRenderer, 0, 0, BUTTONS_WIDTH - 2, BUTTONS_HEIGHT - 2, new TranslatableText(setting.getAsText()), () -> setting.getAsText());
		editButton.setText(setting.getAsText());
		editButton.setChangedListener((text) -> {
			setting.setFromText(text);
			onSettingChanged();
		});
		editButton.setEditable(buttonsActive);
		editButton.setFocusUnlocked(buttonsActive);
		buttons.add(editButton);
	}
	
	@Override
	public void renderButtons(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		editButton.x = x + titleWidth + 1;
		editButton.y = y + 1;
		editButton.render(matrices, mouseX, mouseY, tickDelta);
	}
	
	@Override
	public void reset() {
		setting.reset();
		onSettingChanged();
		updateButtonLabels();
	}
	
	@Override
	protected void onSettingChanged() {
		resetButton.active = !setting.isDefault();
	}
	
	@Override
	public void updateButtonLabels() {
		editButton.setText(setting.getAsText());
	}
	
	@Override
	public void tick() {
		editButton.tick();
	}
}
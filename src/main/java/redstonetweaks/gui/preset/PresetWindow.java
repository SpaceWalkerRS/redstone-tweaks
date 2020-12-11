package redstonetweaks.gui.preset;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.gui.RTWindow;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class PresetWindow extends RTWindow {
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 105;
	
	private final PresetsTab parent;
	
	private RTTextFieldWidget nameField;
	private boolean nameAlreadyExists;
	private RTTextFieldWidget descriptionField;
	private RTButtonWidget modeButton;
	
	public PresetWindow(PresetsTab parent) {
		super(parent.screen, new TranslatableText("Preset Properties"), (parent.screen.getWidth() - WIDTH) / 2, (parent.screen.getHeight() + parent.screen.getHeaderHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		
		this.parent = parent;
	}
	
	@Override
	protected void initContents() {
		int x = getX() + 80;
		int y = getY() + 30;
		
		nameField = new RTTextFieldWidget(screen.getTextRenderer(), 16, x, y, 100, 20, (textField) -> {}, (text) -> {
			parent.getPresetEditor().setName(text);
			
			Preset existingPreset = Presets.fromName(text);
			nameAlreadyExists = existingPreset != null && existingPreset != parent.getPresetEditor().getPreset();
		});
		nameField.setText(parent.getPresetEditor().getName());
		addContent(nameField);
		
		descriptionField = new RTTextFieldWidget(screen.getTextRenderer(), 256, x, y + 22, 300, 20, (textField) -> {}, (text) -> {
			parent.getPresetEditor().setDescription(text);
		});
		descriptionField.setText(parent.getPresetEditor().getDescription());
		addContent(descriptionField);
		
		modeButton = new RTButtonWidget(x, y + 44, 100, 20, () -> new TranslatableText(parent.getPresetEditor().getMode().toString()), (button) -> {
			if (Screen.hasShiftDown()) {
				parent.getPresetEditor().nextMode();
			} else {
				parent.getPresetEditor().previousMode();
			}
			
			button.updateMessage();
		});
		addContent(modeButton);
		
		if (!parent.getPresetEditor().isEditable()) {
			nameField.setActive(false);
			descriptionField.setActive(false);
			modeButton.setActive(false);
		}
	}
	
	@Override
	protected void tickContents() {
		nameField.tick();
		descriptionField.tick();
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int x = getX() + 5;
		int y = getY() + 36;
		screen.client.textRenderer.drawWithShadow(matrices, "Name", x, y, TEXT_COLOR);
		screen.client.textRenderer.drawWithShadow(matrices, "Description", x, y + 22, TEXT_COLOR);
		screen.client.textRenderer.drawWithShadow(matrices, "Mode", x, y + 44, TEXT_COLOR);
		
		if (nameAlreadyExists) {
			screen.client.textRenderer.drawWithShadow(matrices, new TranslatableText("That name already exists!").formatted(Formatting.RED), nameField.getX() + nameField.getWidth() + 5, y, TEXT_COLOR);
		}
		
		nameField.render(matrices, mouseX, mouseY, delta);
		descriptionField.render(matrices, mouseX, mouseY, delta);
		modeButton.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	protected void onRefresh() {
		
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return getFocused() == nameField || getFocused() == descriptionField;
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		if (nameField != except) {
			nameField.unFocus();
		}
		if (descriptionField != except) {
			descriptionField.unFocus();
		}
	}
}
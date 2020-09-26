package redstonetweaks.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class RedstoneTweaksMenuScreen extends Screen {
	
	private final Screen parent;
	
	private SettingsListWidget settingsList;
	
	private TextFieldWidget searchBox;
	private ButtonWidget resetButton;
	
	public RedstoneTweaksMenuScreen(MinecraftClient client) {
		super(new TranslatableText("Redstone Tweaks Menu"));
		
		this.client = client;
		this.parent = client.currentScreen;
	}
	
	@Override
	public void tick() {
		searchBox.tick();
	}
	
	@Override
	protected void init() {
		searchBox = new TextFieldWidget(textRenderer, 5, 25, width - 60 - 10, 20, searchBox, new TranslatableText("search setting"));
		searchBox.setChangedListener((query) -> {
			settingsList.filter(query);
		});
		children.add(searchBox);
		
		resetButton = addButton(new ButtonWidget(width - 60, 25, 50, 20, new TranslatableText("RESET"), (buttonWidget) -> {
			for (SettingsListWidget.Entry entry : settingsList.children()) {
				if (entry instanceof SettingsListWidget.SettingEntry) {
					((SettingsListWidget.SettingEntry)entry).reset();
				}
			}
		}));
		children.add(resetButton);
		
		settingsList = new SettingsListWidget(this, client);
		children.add(settingsList);
	}
	
	@Override
	public void onClose() {
		client.openScreen(parent);
	}
	
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers) ? true : searchBox.keyPressed(keyCode, scanCode, modifiers);
	}
	
	public boolean charTyped(char chr, int keyCode) {
		return searchBox.isFocused() ? searchBox.charTyped(chr, keyCode) : super.charTyped(chr, keyCode);
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		
		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, 8, 16777215);
		searchBox.render(matrices, mouseX, mouseY, delta);
		resetButton.render(matrices, mouseX, mouseY, delta);
		settingsList.render(matrices, mouseX, mouseY, delta);
		
		super.render(matrices, mouseX, mouseY, delta);
	}
}

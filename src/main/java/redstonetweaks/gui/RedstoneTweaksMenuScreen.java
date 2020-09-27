package redstonetweaks.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.settings.types.ISetting;

public class RedstoneTweaksMenuScreen extends Screen {

	private static String lastSearchQuery = "";

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
		settingsList.tick();
	}

	@Override
	protected void init() {
		settingsList = new SettingsListWidget(this, client);
		children.add(settingsList);

		searchBox = new TextFieldWidget(textRenderer, 5, 25, width - 50 - 10, 20, searchBox,
				new TranslatableText("search setting"));
		searchBox.setChangedListener((query) -> {
			settingsList.filter(query);
			lastSearchQuery = query;
		});
		searchBox.setText(lastSearchQuery);
		children.add(searchBox);

		resetButton = addButton(new ButtonWidget(width - 50, 25, 40, 20, new TranslatableText("RESET"), (buttonWidget) -> {
					for (SettingsListWidget.Entry entry : settingsList.children()) {
						if (entry instanceof SettingsListWidget.SettingEntry) {
							((SettingsListWidget.SettingEntry) entry).reset();
						}
					}
		}));
		resetButton.active = ((MinecraftClientHelper)client).getSettingsManager().canChangeSettings();
		children.add(resetButton);
	}

	@Override
	public void onClose() {
		client.openScreen(parent);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers) ? true : searchBox.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return searchBox.isFocused() ? searchBox.charTyped(chr, keyCode) : super.charTyped(chr, keyCode);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, 8, 16777215);
		searchBox.render(matrices, mouseX, mouseY, delta);
		resetButton.render(matrices, mouseX, mouseY, delta);
		settingsList.render(matrices, mouseX, mouseY, delta);

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Element el : children()) {
			if (el.mouseClicked(mouseX, mouseY, button)) {
				setFocused(el);
			}
		}
		
		if (button == 0) {
			setDragging(true);
		}
		
		return true;
	}
	
	public void settingChangedOnServer(ISetting setting) {
		
	}
}

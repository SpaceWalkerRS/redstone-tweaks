package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.settings.Settings;
import redstonetweaks.settings.SettingsPack;
import redstonetweaks.settings.types.ISetting;

public class RTSettingsListWidget extends RTListWidget<RTSettingsListWidget.Entry> {
	
	public RTSettingsListWidget(RTMenuScreen screen, int x, int y, int width, int height) {
		super(screen, x, y, width, height, 22);
		
		for (SettingsPack pack : Settings.SETTINGS_PACKS) {
			addEntry(new SettingsPackEntry(pack));
			
			for (ISetting setting : pack.getSettings()) {
				addEntry(new SettingEntry(client, this, setting));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
			}
			
			addEntry(new SeparatorEntry());
		}
		
		for (Entry entry : children()) {
			entry.init(getEntryTitleWidth());
		}
		
		setScrollAmount(savedScrollAmount);
	}
	
	@Override
	protected int getMaxPosition() {
		return (getItemCount() - 1) * itemHeight + headerHeight;
	}
	
	@Override
	protected void filterEntries(String query) {
		clearEntries();
		
		for (SettingsPack pack : Settings.SETTINGS_PACKS) {
			if (pack.getName().toLowerCase().contains(query)) {
				addEntry(new SettingsPackEntry(pack));
				
				for (ISetting setting : pack.getSettings()) {
					addEntry(new SettingEntry(client, this, setting));
					
					updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
				}
				
				addEntry(new SeparatorEntry());
			} else {
				List<Entry> filteredEntries = new ArrayList<>();
				
				for (ISetting setting : pack.getSettings()) {
					if (setting.getName().toLowerCase().contains(query)) {
						filteredEntries.add(new SettingEntry(client, this, setting));
						
						updateEntryTitleWidth(client.textRenderer.getWidth(setting.getName()));
					}
				}
				
				if (filteredEntries.size() > 0) {
					addEntry(new SettingsPackEntry(pack));
					
					children().addAll(filteredEntries);
					
					addEntry(new SeparatorEntry());
				}
			}
		}
		
		for (Entry entry : children()) {
			entry.init(getEntryTitleWidth());
		}
	}
	
	public void reset() {
		for (Entry entry : children()) {
			if (entry instanceof SettingEntry) {
				((SettingEntry)entry).reset();
			}
		}
	}
	
	public class SettingsPackEntry extends Entry {
		
		private Text title;
		
		public SettingsPackEntry(SettingsPack pack) {
			title = new TranslatableText(pack.getName());
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int textX = getX() + 25;
			int textY = y + 5;
			client.textRenderer.draw(matrices, title, textX, textY, TEXT_COLOR);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return Collections.emptyList();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		public void unfocusTextFields() {
			
		}
	}
	
	public class SeparatorEntry extends Entry {
		
		public SeparatorEntry() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return Collections.emptyList();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		public void unfocusTextFields() {
			
		}
	}
	
	public class SettingEntry extends Entry {
		
		private final ISetting setting;
		private final Text title;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		private final RTButtonWidget resetButton;
		private final boolean buttonsActive;
		
		public SettingEntry(MinecraftClient client, RTSettingsListWidget list, ISetting setting) {
			this.setting = setting;
			this.title = new TranslatableText(setting.getName());
			this.children = new ArrayList<>();
			
			this.buttonPanel = new ButtonPanel(screen);
			this.setting.populateButtonPanel(buttonPanel);
			this.buttonPanel.addAction(() -> onSettingChanged());
			this.children.add(buttonPanel);
			
			this.resetButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("RESET"), (resetButton) -> {
				reset();
			});
			this.children.add(resetButton);
			
			this.buttonsActive = ((MinecraftClientHelper)client).getSettingsManager().canChangeSettings();
		}
		
		// use hovered to render tooltip
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int itemHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			buttonPanel.render(matrices, x, y, mouseX, mouseY, tickDelta);
			
			resetButton.setY(y);
			resetButton.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int entryTitleWidth) {
			buttonPanel.setX(getX() + entryTitleWidth);
			resetButton.setX(getX() + entryTitleWidth + buttonPanel.getWidth() + 5);
			
			buttonPanel.setActive(buttonsActive);
			resetButton.active = buttonsActive && !setting.isDefault();
		}
		
		@Override
		public void tick() {
			buttonPanel.tick();
		}
		
		@Override
		public void unfocusTextFields() {
			buttonPanel.unfocusTextFields();
		}
		
		public void reset() {
			setting.reset();
			onSettingChanged();
			buttonPanel.updateButtonLabels();
		}
		
		protected void onSettingChanged() {
			resetButton.active = buttonsActive && !setting.isDefault();
			
			System.out.println("redo setting packets and gui on setting changed");
			((MinecraftClientHelper)client).getSettingsManager().onSettingChanged(setting);
		}
	}
	
	public static abstract class Entry extends RTListWidget.Entry<RTSettingsListWidget.Entry> {
		
		public void init(int entryTitleWidth) {
			
		}
	}
}

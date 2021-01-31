package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.TextFormatting;

public class RemovedPresetsListWidget extends RTListWidget<RemovedPresetsListWidget.Entry> {
	
	private final RemovedPresetsWindow parent;
	
	public RemovedPresetsListWidget(RemovedPresetsWindow parent, int x, int y, int width, int height) {
		super(parent.screen, x, y, width, height, 22, "Removed Presets List");
		
		this.parent = parent;
	}
	
	@Override
	protected void initList() {
		for (Preset preset : Presets.ALL.values()) {
			if (!Presets.ACTIVE.containsValue(preset)) {
				addEntry(new PresetEntry(preset));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
			}
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		for (Preset preset : Presets.ALL.values()) {
			if (!Presets.ACTIVE.containsValue(preset) && preset.getName().toLowerCase().contains(query)) {
				addEntry(new PresetEntry(preset));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
			}
		}
	}
	
	public void updateButtonsActive() {
		for (Entry e : children()) {
			e.updateButtonsActive();
		}
	}
	
	public class PresetEntry extends Entry {
		
		private final Preset preset;
		private final Text title;
		private final List<Text> tooltip;
		private final List<RTElement> children;
		private final RTButtonWidget unremoveButton;
		
		private boolean trimmedDescription;
		private String description;
		
		public PresetEntry(Preset preset) {
			this.preset = preset;
			this.title = new TranslatableText(preset.getName()).formatted(Formatting.UNDERLINE);
			this.tooltip = createTooltip();
			this.children = new ArrayList<>();
			
			this.unremoveButton = new RTButtonWidget(0, 0, 55, 20, () -> new TranslatableText("Put Back"), (button) -> {
				parent.close();
				
				parent.parent.editPreset(this.preset);
			});
			this.children.add(this.unremoveButton);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int titleWidth) {
			unremoveButton.setX(getX() + getWidth() - unremoveButton.getWidth() - 10);
			
			int width = unremoveButton.getX() - getX() - titleWidth - 25;
			description = TextFormatting.prettyTrimToWidth(preset.getDescription(), width, client.textRenderer);
			if (!description.equals(preset.getDescription())) {
				trimmedDescription = true;
			}
			
			updateButtonsActive();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.drawWithShadow(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			client.textRenderer.drawWithShadow(matrices, description, x + getEntryTitleWidth() + 10, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			unremoveButton.setY(y);
			unremoveButton.render(matrices, mouseX, mouseY, tickDelta);
			
			if (trimmedDescription && hovered && descriptionHovered(mouseX, mouseY)) {
				currentTooltip = tooltip;
			}
		}
		
		private List<Text> createTooltip() {
			List<Text> tooltip = new ArrayList<>();
			for (String line : TextFormatting.getAsLines(preset.getDescription())) {
				tooltip.add(new TranslatableText(line));
			}
			return tooltip;
		}
		
		private boolean descriptionHovered(int mouseX, int mouseY) {
			int x1 = getX() + getEntryTitleWidth() + 10;
			int x2 = unremoveButton.getX() - 10;
			int height = client.textRenderer.fontHeight;
			
			return mouseX >= x1 && mouseX <= x2 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
		
		@Override
		public void updateButtonsActive() {
			unremoveButton.setActive(((RTIMinecraftClient)screen.client).getPresetsManager().canEditPresets());
		}
	}
	
	public abstract class Entry extends RTListWidget.Entry<RemovedPresetsListWidget.Entry> {
		
		public abstract void updateButtonsActive();
	}
}

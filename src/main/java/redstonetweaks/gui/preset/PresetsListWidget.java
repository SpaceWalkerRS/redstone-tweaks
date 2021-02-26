package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.util.TextFormatting;

public class PresetsListWidget extends RTListWidget<PresetsListWidget.Entry> {
	
	private final PresetsTab parent;
	
	private ViewMode viewMode;
	
	public PresetsListWidget(PresetsTab parent, int x, int y, int width, int height) {
		super(parent.screen, x, y, width, height, 22, "Presets List");
		
		this.parent = parent;
		
		this.viewMode = ViewMode.ALL;
	}
	
	@Override
	protected void initList() {
		if (viewMode == ViewMode.ALL || viewMode == ViewMode.GLOBAL) {
			addEntry(new EnvEntry(false));
			
			for (Preset preset : Presets.getActiveGlobalPresets()) {
				addEntry(new PresetEntry(preset));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
			}
			
			addEntry(new SeparatorEntry());
		}
		
		if (viewMode == ViewMode.ALL || viewMode == ViewMode.LOCAL) {
			addEntry(new EnvEntry(true));
			
			for (Preset preset : Presets.getActiveLocalPresets()) {
				addEntry(new PresetEntry(preset));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
			}
			
			addEntry(new SeparatorEntry());
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		if (viewMode == ViewMode.ALL || viewMode == ViewMode.GLOBAL) {
			addEntry(new EnvEntry(false));
			
			for (Preset preset : Presets.getActiveGlobalPresets()) {
				if (preset.getName().toLowerCase().contains(query)) {
					addEntry(new PresetEntry(preset));
					
					updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
				}
			}
			
			addEntry(new SeparatorEntry());
		}
		
		if (viewMode == ViewMode.ALL || viewMode == ViewMode.LOCAL) {
			addEntry(new EnvEntry(true));
			
			for (Preset preset : Presets.getActiveLocalPresets()) {
				if (preset.getName().toLowerCase().contains(query)) {
					addEntry(new PresetEntry(preset));
					
					updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
				}
			}
			
			addEntry(new SeparatorEntry());
		}
	}
	
	public void updateButtonsActive() {
		for (Entry e : children()) {
			e.updateButtonsActive();
		}
	}
	
	public ViewMode getViewMode() {
		return viewMode;
	}
	
	public void updateViewMode(boolean next) {
		setViewMode(next ? viewMode.next() : viewMode.previous());
	}
	
	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}
	
	public class SeparatorEntry extends Entry {
		
		public SeparatorEntry() {
			
		}
		
		@Override
		public void updateButtonsActive() {
			
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return Collections.emptyList();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			
		}
	}
	
	public class EnvEntry extends Entry {
		
		private static final String GLOBAL_TOOLTIP_TEXT = "Global presets are stored in the game's run directory and are therefor available in every world.";
		private static final String LOCAL_TOOLTIP_TEXT = "Local presets are stored in this world's save folder and are therefor only available in this world.";
		
		private final Text text;
		private final List<Text> tooltip;
		
		public EnvEntry(boolean local) {
			this.text = new TranslatableText(String.format("%s Presets", local ? "Local" : "Global")).formatted(Formatting.BOLD, Formatting.UNDERLINE);
			this.tooltip = createTooltip(local);
		}
		
		@Override
		public void updateButtonsActive() {
			
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return Collections.emptyList();
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			drawCenteredText(matrices, client.textRenderer, text, getWidth() / 2, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			if (hovered && titleHovered(mouseX, mouseY)) {
				currentTooltip = tooltip;
			}
		}
		
		private List<Text> createTooltip(boolean local) {
			List<Text> tooltip = new ArrayList<>();
			for (String line : TextFormatting.getAsLines(local ? LOCAL_TOOLTIP_TEXT : GLOBAL_TOOLTIP_TEXT)) {
				tooltip.add(new TranslatableText(line));
			}
			
			return tooltip;
		} 
		
		private boolean titleHovered(int mouseX, int mouseY) {
			int width = client.textRenderer.getWidth(text);
			int height = client.textRenderer.fontHeight;
			
			int centerX = getWidth() / 2;
			
			return mouseX >= centerX - width / 2 && mouseX  <= centerX + width / 2 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
	}
	
	public class PresetEntry extends Entry {
		
		private final Preset preset;
		private final Text title;
		private final List<Text> tooltip;
		private final List<RTElement> children;
		private final RTButtonWidget applyButton;
		private final RTButtonWidget duplicateButton;
		private final RTButtonWidget editButton;
		private final RTButtonWidget deleteButton;
		
		private boolean trimmedDescription;
		private String description;
		
		public PresetEntry(Preset preset) {
			this.preset = preset;
			this.title = new TranslatableText(preset.getName()).formatted(Formatting.UNDERLINE, this.preset.isEditable() ? Formatting.UNDERLINE : Formatting.BOLD);
			this.tooltip = createTooltip();
			this.children = new ArrayList<>();
			
			this.applyButton = new RTButtonWidget(0, 0, 40, 20, () -> new TranslatableText("Apply"), (button) -> {
				((RTIMinecraftClient)screen.client).getPresetsManager().applyPreset(this.preset);
			});
			this.children.add(this.applyButton);
			
			this.duplicateButton = new RTButtonWidget(0, 0, 60, 20, () -> new TranslatableText("Duplicate"), (button) -> {
				parent.editPreset(Presets.duplicatePreset(this.preset));
			});
			this.children.add(this.duplicateButton);
			
			this.editButton = new RTButtonWidget(0, 0, 34, 20, () -> new TranslatableText(PermissionManager.canEditPresets(client.player) && this.preset.isEditable() ? "Edit" : "View"), (button) -> {
				parent.editPreset(this.preset);
			});
			this.children.add(this.editButton);
			
			this.deleteButton = new RTButtonWidget(0, 0, 45, 20, () -> new TranslatableText("Delete"), (button) -> {
				((RTIMinecraftClient)screen.client).getPresetsManager().removePreset(this.preset);
			});
			this.children.add(deleteButton);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int titleWidth) {
			deleteButton.setX(getX() + getWidth() - deleteButton.getWidth() - 10);
			editButton.setX(deleteButton.getX() - editButton.getWidth() - 2);
			duplicateButton.setX(editButton.getX() - duplicateButton.getWidth() - 2);
			applyButton.setX(duplicateButton.getX() - applyButton.getWidth() - 5);
			
			int width = applyButton.getX() - getX() - titleWidth - 25;
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (hovered) {
				fillGradient(matrices, 2, y - 1, getScrollbarPositionX() - 1, y + entryHeight - 1, -2146365166, -2146365166);
			}
			
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			client.textRenderer.draw(matrices, description, x + getEntryTitleWidth() + 10, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			applyButton.setY(y);
			applyButton.render(matrices, mouseX, mouseY, tickDelta);
			
			duplicateButton.setY(y);
			duplicateButton.render(matrices, mouseX, mouseY, tickDelta);
			
			editButton.setY(y);
			editButton.render(matrices, mouseX, mouseY, tickDelta);
			
			deleteButton.setY(y);
			deleteButton.render(matrices, mouseX, mouseY, tickDelta);
			
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
			int x2 = applyButton.getX() - 10;
			int height = client.textRenderer.fontHeight;
			
			return mouseX >= x1 && mouseX <= x2 && mouseY % itemHeight >= 0 && mouseY % itemHeight <= height;
		}
		
		@Override
		public void updateButtonsActive() {
			boolean canEditPresets = PermissionManager.canEditPresets(client.player);
			
			applyButton.setActive(canEditPresets);
			duplicateButton.setActive(canEditPresets);
			editButton.updateMessage();
			deleteButton.setActive(preset.isEditable() && canEditPresets);
		}
	}
	
	public abstract class Entry extends RTListWidget.Entry<PresetsListWidget.Entry> {
		
		public abstract void updateButtonsActive();
		
	}
	
public enum ViewMode {
		
		ALL(0),
		GLOBAL(1),
		LOCAL(2);
		
		private static final ViewMode[] MODES;
		
		static {
			MODES = new ViewMode[values().length];
			
			for (ViewMode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		
		private ViewMode(int index) {
			this.index = index;
		}
		
		public static ViewMode fromIndex(int index) {
			if (index < 0) {
				return MODES[MODES.length - 1];
			}
			if (index >= MODES.length) {
				return MODES[0];
			}
			
			return MODES[index];
		}
		
		public ViewMode next() {
			return fromIndex(index + 1);
		}
		
		public ViewMode previous() {
			return fromIndex(index - 1);
		}
	}
}

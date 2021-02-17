package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
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
	
	public PresetsListWidget(PresetsTab parent, int x, int y, int width, int height) {
		super(parent.screen, x, y, width, height, 22, "Presets List");
		
		this.parent = parent;
	}
	
	@Override
	protected void initList() {
		for (Preset preset : Presets.getActivePresets()) {
			addEntry(new PresetEntry(preset));
			
			updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
		}
		
		addEntry(new AddPresetEntry());
	}
	
	@Override
	protected void filterEntries(String query) {
		for (Preset preset : Presets.getActivePresets()) {
			if (preset.getName().toLowerCase().contains(query)) {
				addEntry(new PresetEntry(preset));
				
				updateEntryTitleWidth(client.textRenderer.getWidth(preset.getName()));
			}
		}
		
		addEntry(new AddPresetEntry());
	}
	
	public void updateButtonsActive() {
		for (Entry e : children()) {
			e.updateButtonsActive();
		}
	}
	
	public class AddPresetEntry extends Entry {
		
		private final List<RTElement> children;
		private final RTButtonWidget addButton;
		private final RTButtonWidget oopsButton;
		
		public AddPresetEntry() {
			this.children = new ArrayList<>();
			
			this.addButton = new RTButtonWidget((getX() + getWidth() - 80) / 2, 0, 80, 20, () -> new TranslatableText("New Preset"), (button) -> {
				parent.newPreset(Screen.hasShiftDown());
			});
			this.children.add(this.addButton);
			
			this.oopsButton = new RTButtonWidget(getX() + getWidth() - 55, 0, 45, 20, () -> new TranslatableText("oops"), (button) -> {
				screen.openWindow(new RemovedPresetsWindow(parent));
			});
			this.children.add(this.oopsButton);
			
			updateButtonsActive();
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
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
			addButton.setY(y);
			addButton.render(matrices, mouseX, mouseY, tickDelta);
			
			oopsButton.setY(y);
			oopsButton.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		public void updateButtonsActive() {
			boolean canEditPresets = PermissionManager.canEditPresets(client.player);
			
			addButton.setActive(canEditPresets);
			oopsButton.setActive(canEditPresets);
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
		
		private double hoverAnimation;
		
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
			
			this.hoverAnimation = 0.0F;
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
		protected boolean hasFocusedTextField() {
			return false;
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			double speed = 60.0D / ((RTIMinecraftClient)client).getCurrentFps();
			if (hovered) {
				hoverAnimation = 1.0D - (1.0D - hoverAnimation) / Math.pow(1.2D, speed);
			} else {
				hoverAnimation = hoverAnimation / Math.pow(2, speed);
			}
			fillGradient(matrices, 2, y - 1, (int)(hoverAnimation * (getScrollbarPositionX() - 1)), y + entryHeight - 1, -2146365166, -2146365166);
			
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
}

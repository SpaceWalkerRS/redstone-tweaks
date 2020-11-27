package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public class PresetsListWidget extends RTListWidget<PresetsListWidget.Entry> {
	
	public PresetsListWidget(RTMenuScreen screen, int x, int y, int width, int height) {
		super(screen, x, y, width, height, 22, "Presets");
	}
	
	@Override
	protected void initList() {
		for (Preset preset : Presets.ALL) {
			addEntry(new Entry(preset));
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		
	}
	
	public class Entry extends RTListWidget.Entry<PresetsListWidget.Entry> {
		
		private final Preset preset;
		private final Text title;
		private final List<RTElement> children;
		private final RTButtonWidget applyButton;
		
		public Entry(Preset preset) {
			this.preset = preset;
			this.title = new TranslatableText(preset.getName());
			this.children = new ArrayList<>();
			
			this.applyButton = new RTButtonWidget(200, 0, 50, 20, () -> new TranslatableText("Apply"), (button) -> {
				this.preset.apply();
			});
			this.children.add(this.applyButton);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		public void unfocusTextFields() {
			
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			applyButton.setY(y);
			applyButton.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
}

package redstonetweaks.gui.hotkeys;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.hotkeys.HotKeyManager;
import redstonetweaks.hotkeys.RTKeyBinding;

public class HotkeysListWidget extends RTListWidget<HotkeysListWidget.Entry> {
	
	private static double savedScrollAmount;
	
	private final HotkeysTab parent;
	
	public HotkeysListWidget(HotkeysTab parent, int x, int y, int width, int height) {
		super(parent.screen, x, y, width, height, 22);
		
		this.parent = parent;
		
		for (RTKeyBinding keyBinding : HotKeyManager.getKeyBindings()) {
			addEntry(new Entry(keyBinding));
			
			updateEntryTitleWidth(client.textRenderer.getWidth(keyBinding.getName()));
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

	}
	
	public void saveScrollAmount() {
		savedScrollAmount = getScrollAmount();
	}
	
	public void onHotkeyChanged(RTKeyBinding keyBinding) {
		for (Entry entry : children()) {
			if (entry.keyBinding == keyBinding || keyBinding == null) {
				entry.onHotkeyChanged();
			}
		}
	}
	
	public class Entry extends RTListWidget.Entry<HotkeysListWidget.Entry> {
		
		public final RTKeyBinding keyBinding;
		private final Text title;
		private final List<RTElement> children;
		private final RTButtonWidget editButton;
		private final RTButtonWidget resetButton;
		
		public Entry(RTKeyBinding keyBinding) {
			this.keyBinding = keyBinding;
			this.title = new TranslatableText(keyBinding.getName());
			this.children = new ArrayList<>();
			
			this.editButton = new RTButtonWidget(0, 0, 75, 20, () -> {
				if (parent.focusedKeyBinding == keyBinding) {
					return new TranslatableText("> " + keyBinding.getKey().getLocalizedText().asString() + " <").formatted(Formatting.YELLOW);
				} else {
					return keyBinding.getKey().getLocalizedText();
				}
			}, (button) -> {
				parent.focusedKeyBinding = keyBinding;
				button.updateMessage();
			});
			this.children.add(editButton);
			
			this.resetButton = new RTButtonWidget(0, 0, 50, 20, () -> new TranslatableText("RESET"), (button) -> {
				HotKeyManager.updateKeyBinding(keyBinding, keyBinding.getDefaultKey());
			});
			this.children.add(resetButton);
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
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			editButton.setY(y);
			editButton.render(matrices, mouseX, mouseY, tickDelta);
			
			resetButton.setY(y);
			resetButton.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		public void init(int entryTitleWidth) {
			editButton.setX(getX() + entryTitleWidth);
			resetButton.setX(editButton.getX() + editButton.getWidth() + 5);
			
			resetButton.setActive(!keyBinding.isDefault());
		}
		
		public void onHotkeyChanged() {
			editButton.updateMessage();
			resetButton.setActive(!keyBinding.isDefault());
		}
		
		@Override
		protected boolean hasFocusedTextField() {
			return false;
		}
	}
}
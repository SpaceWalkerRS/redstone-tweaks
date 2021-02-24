package redstonetweaks.gui.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import redstonetweaks.gui.ButtonPanel;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.setting.types.ArraySetting;
import redstonetweaks.setting.types.ISetting;

public class ArraySettingListWidget<K, E> extends RTListWidget<ArraySettingListWidget<K, E>.Entry> {
	
	private final ArraySetting<K, E> setting;
	private final E[] array;
	private final Consumer<ISetting> changeListener;
	
	public ArraySettingListWidget(RTMenuScreen screen, int x, int y, int width, int height, ArraySetting<K, E> setting, E[] array, Consumer<ISetting> changeListener) {
		super(screen, x, y, width, height, 22, setting.getId());
		
		this.setting = setting;
		this.array = array;
		this.changeListener = changeListener;
	}
	
	@Override
	protected void initList() {
		for (int index = 0; index < array.length; index++) {
			addEntry(new Entry(index));
			
			updateEntryTitleWidth(client.textRenderer.getWidth(setting.getKeyAsString(index)));
		}
	}
	
	@Override
	protected void filterEntries(String query) {
		
	}
	
	public void disableButtons() {
		for (Entry e : children()) {
			e.disableButtons();
		}
	}
	
	public class Entry extends RTListWidget.Entry<ArraySettingListWidget<K, E>.Entry> {
		
		private final int index;
		private final Text title;
		private final List<RTElement> children;
		private final ButtonPanel buttonPanel;
		
		public Entry(int index) {
			this.index = index;
			this.title = new TranslatableText(setting.getKeyAsString(index));
			this.children = new ArrayList<>();
			
			this.buttonPanel = new ButtonPanel();
			this.populateButtonPanel();
			this.children.add(buttonPanel);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void allowHover(boolean allowHover) {
			children.forEach((element) -> element.allowHover(allowHover));
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.drawWithShadow(matrices, title, x, y + itemHeight / 2 - 5, TEXT_COLOR);
			
			buttonPanel.setY(y);
			buttonPanel.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		public void tick() {
			buttonPanel.tick();
		}
		
		@Override
		public void init(int titleWidth) {
			buttonPanel.setX(getX() + getWidth() - 110);
		}
		
		private void populateButtonPanel() {
			if (array instanceof Boolean[]) {
				Boolean[] bArray = (Boolean[])array;
				buttonPanel.addButton(new RTButtonWidget(0, 0, 100, 20, () -> {
					Formatting color = bArray[index] ? Formatting.GREEN : Formatting.RED;
					return new TranslatableText(setting.elementToString(array[index])).formatted(color);
				}, (button) -> {
					//System.out.println(array);
					//System.out.println(bArray);
					bArray[index] = !bArray[index];
					changeListener.accept(setting);
					button.updateMessage();
				}));
			}
		}
		
		private void disableButtons() {
			buttonPanel.setActive(false);
		}
	}
}

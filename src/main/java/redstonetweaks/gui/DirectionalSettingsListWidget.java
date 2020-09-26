package redstonetweaks.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import redstonetweaks.settings.types.DirectionalSetting;

public class DirectionalSettingsListWidget extends ElementListWidget<DirectionalSettingsListWidget.Entry> {
	
	private final DirectionalSetting<?> setting;
	
	public DirectionalSettingsListWidget(Screen parent, MinecraftClient client, DirectionalSetting<?> setting) {
		super(client, 200, 200, (parent.height - 200) / 2, (parent.height + 200) / 2, 22);
		this.setting = setting;
		
		for (Direction dir : Direction.values()) {
			addEntry(new Entry(dir));
		}
	}
	
	@Override
	public int getRowWidth() {
		return 200;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderList(matrices, 0, 0, mouseX, mouseY, delta);
	}
	
	public class Entry extends ElementListWidget.Entry<DirectionalSettingsListWidget.Entry> {
		
		private final Direction direction;
		private final Text title;
		private final AbstractButtonWidget editButton;
		
		public Entry(Direction direction) {
			this.direction = direction;
			title = new TranslatableText(direction.getName());
			editButton = setting.createEditButton(direction);
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x + 5, y + entryHeight / 2, 16777215);
			
			editButton.x = x + 80;
			editButton.y = y + 3 + (editButton instanceof TextFieldWidget ? 1 : 0);
			editButton.render(matrices, mouseX, mouseY, tickDelta);
		}
		
		@Override
		public List<? extends Element> children() {
			return Arrays.asList(editButton);
		}
	}
}

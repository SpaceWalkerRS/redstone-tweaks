package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import redstonetweaks.settings.Settings;
import redstonetweaks.settings.SettingsPack;
import redstonetweaks.settings.types.ISetting;

public class SettingsListWidget extends ElementListWidget<SettingsListWidget.Entry> {
	
	public static final int SCROLLBAR_WIDTH = 6;
	
	public int maxSettingNameLength;

	public SettingsListWidget(Screen parent, MinecraftClient client) {
		super(client, parent.width, parent.height, 70, parent.height - 5, 22);
		
		for (SettingsPack pack : Settings.SETTINGS_PACKS) {
			addEntry(new SettingsPackEntry(pack));
			
			for (ISetting setting : pack.getSettings()) {
				addEntry(setting.createGUIEntry(client));
				
				int settingNameLength = client.textRenderer.getWidth(setting.getName());
				if (settingNameLength > maxSettingNameLength) {
					maxSettingNameLength = settingNameLength;
				}
			}
			
			addEntry(new SeparatorEntry());
		}
		
		children().forEach((entry) -> {
			if (entry instanceof SettingEntry) {
				((SettingEntry)entry).setTitleWith(maxSettingNameLength);
			}
		});
	}
	
	public void filter(String query) {
		clearEntries();
		maxSettingNameLength = 0;
		
		query = query.toLowerCase();
		
		for (SettingsPack pack : Settings.SETTINGS_PACKS) {
			if (pack.getName().toLowerCase().contains(query)) {
				addEntry(new SettingsPackEntry(pack));
				
				for (ISetting setting : pack.getSettings()) {
					addEntry(setting.createGUIEntry(client));
					
					int settingNameLength = client.textRenderer.getWidth(setting.getName());
					if (settingNameLength > maxSettingNameLength) {
						maxSettingNameLength = settingNameLength;
					}
				}
				
				addEntry(new SeparatorEntry());
			} else {
				List<Entry> entries = new ArrayList<>();
				
				for (ISetting setting : pack.getSettings()) {
					if (setting.getName().toLowerCase().contains(query)) {
						entries.add(setting.createGUIEntry(client));
						
						int settingNameLength = client.textRenderer.getWidth(setting.getName());
						if (settingNameLength > maxSettingNameLength) {
							maxSettingNameLength = settingNameLength;
						}
					}
				}
				
				if (entries.size() > 0) {
					addEntry(new SettingsPackEntry(pack));
					
					children().addAll(entries);
					
					addEntry(new SeparatorEntry());
				}
			}
		}
		
		children().forEach((entry) -> {
			if (entry instanceof SettingEntry) {
				((SettingEntry)entry).setTitleWith(maxSettingNameLength);
			}
		});
	}
	
	private int getMaxScroll() {
		return Math.max(0, getMaxPosition() - (bottom - top - 4));
	}
	
	@Override
	public int getRowWidth() {
		return width;
	}
	
	@Override
	protected int getScrollbarPositionX() {
		return width - 8;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderList(matrices, 0, 0, mouseX, mouseY, delta);
		
		if (getMaxScroll() > 0) {
			renderScrollbar(matrices);
		}
	}
	
	public void renderScrollbar(MatrixStack matrices) {
		int left = width - SCROLLBAR_WIDTH - 2;
		int right = width - 2;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		int maxScroll = getMaxScroll();
		
		int p = MathHelper.clamp(((bottom - top) * (bottom - top)) / getMaxPosition(), 32, bottom - top - 8);
		int q = (int)getScrollAmount() * (bottom - top - p) / maxScroll + top;
		if (q < top) {
			q = top;
		}
		
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		
		bufferBuilder.vertex(left     , bottom   , 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(right    , bottom   , 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(right    , top      , 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(left     , top      , 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 200).next();
		
		bufferBuilder.vertex(left     , p + q    , 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 200).next();
		bufferBuilder.vertex(right    , p + q    , 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 200).next();
		bufferBuilder.vertex(right    , q        , 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 200).next();
		bufferBuilder.vertex(left     , q        , 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 200).next();
		
		bufferBuilder.vertex(left     , p + q - 1, 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 200).next();
		bufferBuilder.vertex(right - 1, p + q - 1, 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 200).next();
		bufferBuilder.vertex(right - 1, q        , 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 200).next();
		bufferBuilder.vertex(left     , q        , 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 200).next();
		
		tessellator.draw();
		
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
	}
	
	public class SettingsPackEntry extends Entry {
		
		private Text title;
		
		public SettingsPackEntry(SettingsPack pack) {
			title = new TranslatableText(pack.getName());
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int textX = 25;
			int textY = y + 5;
			client.textRenderer.draw(matrices, title, textX, textY, 16777215);
		}
		
		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
		}
	}
	
	public class SeparatorEntry extends Entry {
		
		public SeparatorEntry() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			
		}
		
		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
		}
	}
	
	public static abstract class SettingEntry extends Entry {
		
		protected static final int BUTTONS_WIDTH = 100;
		protected static final int BUTTONS_HEIGHT = 20;
		
		protected final MinecraftClient client;
		protected final ISetting setting;
		protected final Text title;
		protected final Text tooltip;
		protected final List<AbstractButtonWidget> buttons;
		protected final ButtonWidget resetButton;
		
		protected int titleWidth;
		
		public SettingEntry(MinecraftClient client, ISetting setting) {
			this.client = client;
			this.setting = setting;
			this.title = new TranslatableText(setting.getName());
			this.tooltip = new TranslatableText(setting.getDescription());
			this.buttons = new ArrayList<>();
			
			this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("RESET"), (resetButton) -> {
				reset();
			});
			resetButton.active = !setting.isDefault();
			buttons.add(resetButton);
		}
		
		public void setTitleWith(int maxSettingNameLength) {
			titleWidth = maxSettingNameLength + 25;
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, title, x + 5, y + (entryHeight / 2) - 3, 16777215);
			
			resetButton.x = x + titleWidth + BUTTONS_WIDTH + 5;
			resetButton.y = y;
			resetButton.render(matrices, mouseX, mouseY, tickDelta);
			
			renderButtons(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
		}
		
		public abstract void renderButtons(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

		@Override
		public List<? extends Element> children() {
			return buttons;
		}
		
		public Text getTooltip() {
			return tooltip;
		}
		
		public void reset() {
			setting.reset();
			onSettingChanged();
		}
		
		protected void onSettingChanged() {
			resetButton.active = !setting.isDefault();
			updateButtonLabels();
			
			System.out.println("redo setting packets and gui on setting changed");
			//((MinecraftClientHelper)client).getSettingsManager().onSettingChanged(setting);
		}
		
		public abstract void updateButtonLabels();
	}
	
	public static abstract class Entry extends ElementListWidget.Entry<SettingsListWidget.Entry> {
		
	}
}

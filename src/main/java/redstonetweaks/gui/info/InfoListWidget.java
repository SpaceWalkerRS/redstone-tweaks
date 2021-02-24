package redstonetweaks.gui.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.gui.RTElement;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.widget.RTButtonWidget;
import redstonetweaks.gui.widget.RTTexturedButtonWidget;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.util.TextFormatting;

public class InfoListWidget extends RTListWidget<InfoListWidget.Entry> {
	
	public InfoListWidget(RTMenuScreen screen) {
		super(screen, 0, screen.getHeaderHeight(), screen.getWidth(), screen.getHeight() - screen.getHeaderHeight() - 5, 18, "Info List");
	}
	
	@Override
	protected void initList() {
		addEntry(new CategoryEntry("Client"));
		addEntry(new InfoEntry().addText("Mod Version: " + RedstoneTweaks.MOD_VERSION));
		addEntry(new SeparatorEntry());
		
		addEntry(new CategoryEntry("Server"));
		
		InfoEntry serverVersionInfo = new InfoEntry();
		RedstoneTweaksVersion serverVersion = ServerInfo.getModVersion();
		if (serverVersion.isValid()) {
			serverVersionInfo.addText(new TranslatableText("Mod Version: "));
			
			Formatting color = Formatting.GREEN;
			List<Text> hoverMessage = null;
			
			if (!serverVersion.equals(RedstoneTweaks.MOD_VERSION)) {
				if (serverVersion.isNewerThan(RedstoneTweaks.MOD_VERSION)) {
					color = Formatting.AQUA;
					hoverMessage = TextFormatting.getAsTextLines(screen.getTextRenderer(), "The server is running a newer version of Redstone Tweaks! You might be missing out on new features and your experience might be sub-optimal. Go to the github page to get the latest release of Redstone Tweaks.");
				} else {
					color = Formatting.GOLD;
					hoverMessage = TextFormatting.getAsTextLines(screen.getTextRenderer(), "The server is running an older version of Redstone Tweaks! Your experience might be sub-optimal, as the server does not have the latest features.");
				}
			}
			
			serverVersionInfo.addText(new TranslatableText(serverVersion.toString()).formatted(color), hoverMessage);
		} else {
			serverVersionInfo.addText(new TranslatableText("Not installed!").formatted(Formatting.RED));
		}
		addEntry(serverVersionInfo);
		
		addEntry(new SeparatorEntry());
		
		addEntry(new CategoryEntry("Credits"));
		addEntry(new InfoEntry().addText("Space Walker - lead developer"));
		addEntry(new InfoEntry().addText("G4me4u - developer"));
		addEntry(new SeparatorEntry());
		
		addEntry(new CategoryEntry("Links"));
		
		String text = "Visit the GitHub page!";
		addEntry(new LinkEntry(text, "https://github.com/SpaceWalkerRS/redstone-tweaks"));
		updateEntryTitleWidth(screen.getTextRenderer().getWidth(text));
		
		text = "Join the Discord guild!";
		addEntry(new LinkEntry(text, "https://discord.gg/EJC9zkX"));
		updateEntryTitleWidth(screen.getTextRenderer().getWidth(text));
	}
	
	@Override
	protected void filterEntries(String query) {
		initList();
	}
	
	private class LinkEntry extends Entry {
		
		private final Text text;
		private final List<RTElement> children;
		private final RTButtonWidget button;
		
		public LinkEntry(String text, String url) {
			this.text = new TranslatableText(text);
			this.children = new ArrayList<>();
			
			this.button = new RTTexturedButtonWidget(0, 0, 20, 20, RTTexturedButtonWidget.WIDGETS_LOCATION, 0, 106, 256, 256, 20, (button) -> {
				saveScrollAmount();
				
				client.openScreen(new ConfirmChatLinkScreen((confirm) -> {
					if (confirm) {
						Util.getOperatingSystem().open(url);
					}
					
					client.openScreen(screen);
				}, url, true));
			});
			children.add(button);
		}
		
		@Override
		public List<? extends RTElement> getChildren() {
			return children;
		}
		
		@Override
		public void init(int titleWidth) {
			button.setX(getX() + titleWidth + 5);
		}
		
		@Override
		public void tick() {
			
		}
		
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			screen.getTextRenderer().draw(matrices, text, x, y + 5, TEXT_COLOR);
			
			button.setY(y - 1);
			button.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
	
	private class SeparatorEntry extends Entry {
		
		public SeparatorEntry() {
			
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
	
	private class CategoryEntry extends Entry {
		
		private final Text name;
		
		public CategoryEntry(String name) {
			this.name = new TranslatableText(name).formatted(Formatting.UNDERLINE);
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
			screen.getTextRenderer().draw(matrices, name, x, y + 5, TEXT_COLOR);
		}
	}
	
	private class InfoEntry extends Entry {
		
		private final List<Text> text;
		private final List<List<Text>> hoverMessage;
		
		public InfoEntry() {
			this.text = new ArrayList<>();
			this.hoverMessage = new ArrayList<>();
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
			boolean cursorOnLine = mouseY >= y && mouseY <= y + entryHeight;
			
			y += 5;
			
			for (int i = 0; i < this.text.size(); i++) {
				Text text = this.text.get(i);
				List<Text> hoverMessage = this.hoverMessage.get(i);
				
				screen.getTextRenderer().draw(matrices, text, x, y, TEXT_COLOR);
				
				if (cursorOnLine && hoverMessage != null && mouseX >= x && mouseX <= x + screen.getTextRenderer().getWidth(text)) {
					currentTooltip = hoverMessage;
				}
				
				x += screen.getTextRenderer().getWidth(text);
			}
		}
		
		public InfoEntry addText(String text) {
			return addText(new TranslatableText(text));
		}
		
		public InfoEntry addText(Text text) {
			return addText(text, null);
		}
		
		public InfoEntry addText(Text text, List<Text> hoverMessage) {
			this.text.add(text);
			this.hoverMessage.add(hoverMessage);
			
			return this;
		}
	}
	
	protected abstract class Entry extends RTListWidget.Entry<InfoListWidget.Entry> {
		
	}
}

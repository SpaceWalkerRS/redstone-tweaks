package redstonetweaks.gui.info;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.RTMenuTab;
import redstonetweaks.interfaces.RTIMinecraftClient;

public class InfoTab extends RTMenuTab {
	
	private Map<String, List<Text>> info;
	
	public InfoTab(RTMenuScreen screen) {
		super(screen, new TranslatableText("Info"));
		
		this.info = new LinkedHashMap<>();
	}
	
	@Override
	protected void initContents() {
		info.clear();
		
		info.put("Client", new ArrayList<>());
		info.put("Server", new ArrayList<>());
		info.put("Credits", new ArrayList<>());
		
		info.get("Client").add(new TranslatableText("Mod Version: " + RedstoneTweaks.MOD_VERSION.toString()));
		
		RedstoneTweaksVersion serverVersion = ((RTIMinecraftClient)this.screen.client).getServerInfo().getModVersion();
		if (serverVersion.isValid()) {
			info.get("Server").add(new TranslatableText("Mod Version: " + serverVersion.toString()));
		} else {
			info.get("Server").add(new TranslatableText("Not installed!"));
		}
		
		info.get("Credits").add(new TranslatableText("Space Walker - lead developer"));
		info.get("Credits").add(new TranslatableText("G4me4u - developer"));
	}
	
	@Override
	protected void tickContents() {
		
	}
	
	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int x = 7;
		int y = screen.getHeaderHeight();
		
		for (String category : info.keySet()) {
			screen.getTextRenderer().draw(matrices, new TranslatableText(category).formatted(Formatting.UNDERLINE), x, y, TEXT_COLOR);
			
			for (Text text : info.get(category)) {
				y += 18;
				screen.getTextRenderer().draw(matrices, text, x, y, TEXT_COLOR);
			}
			
			y += 30;
		}
	}
	
	@Override
	public void onTabClosed() {
		
	}
	
	@Override
	protected boolean hasFocusedTextField() {
		return false;
	}
	
	@Override
	public void unfocusTextFields(Element except) {
		
	}
}

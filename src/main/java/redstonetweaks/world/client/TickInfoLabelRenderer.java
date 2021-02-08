package redstonetweaks.world.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import redstonetweaks.world.common.WorldTickHandler.Task;

public class TickInfoLabelRenderer extends DrawableHelper {
	
	private final static int BACKGROUND_MARGIN_X = 2;
	private final static int BACKGROUND_MARGIN_Y = 2;
	private final static int BACKGROUND_COLOR = 0x50333333;
	private final static int TEXT_MARGIN_X = 1;
	private final static int TEXT_MARGIN_Y = 1;
	private final static int TEXT_SPACING = 1;
	private final static int TEXT_COLOR = 0xFFEEEEEE;
	
	private final TextRenderer font;
	
	private long currentTime = 0L;
	private String currentWorldName;
	private Task currentTask;
	
	private int bgL;
	private int bgU;
	private int bgR;
	private int bgD;
	
	private int tL;
	private int tU;
	
	private String line1;
	private String line2;
	private String line3;
	
	public TickInfoLabelRenderer(MinecraftClient client) {
		this.font = client.textRenderer;
		
		initText();
	}
	
	private void initText() {
		line1 = "Tick: -";
		line2 = "Current world: -";
		line3 = "Current task: -";
	}
	
	public void updateText() {
		line1 = "Tick: " + String.valueOf(currentTime);
		line2 = "Current world: " + (currentWorldName == null ? "-" : currentWorldName);
		line3 = "Current task: " + (currentTask == null || !currentTask.display() ? "-" : currentTask.getName());
	}
	
	public void render(MatrixStack matrixStack) {
		renderBackground(matrixStack);
		renderText(matrixStack);
	}
	
	private void renderBackground(MatrixStack matrixStack) {
		bgL = BACKGROUND_MARGIN_X;
		bgU = BACKGROUND_MARGIN_Y;
		bgR = bgL + getLabelWidth();
		bgD = bgU + getLabelHeight();
		
		fill(matrixStack, bgL, bgU, bgR, bgD, BACKGROUND_COLOR);
	}
	
	private void renderText(MatrixStack matrixStack) {
		tL = bgL + TEXT_MARGIN_X ;
		tU = bgU + TEXT_MARGIN_Y;
		
		font.draw(matrixStack, line1, tL, tU, TEXT_COLOR);
		font.draw(matrixStack, line2, tL, tU + font.fontHeight + TEXT_SPACING, TEXT_COLOR);
		font.draw(matrixStack, line3, tL, tU + 2 * (font.fontHeight + TEXT_SPACING), TEXT_COLOR);
	}
	
	private int getLabelWidth() {
		return getTextWidth() + 2 * TEXT_MARGIN_X;
	}
	
	private int getLabelHeight() {
		return getTextHeight() + 2 * TEXT_MARGIN_Y;
	}
	
	private int getTextWidth() {
		int width1 = font.getWidth(line1);
		int width2 = font.getWidth(line2);
		int width3 = font.getWidth(line3);
		
		return Math.max(Math.max(width1, width2), width3);
	}
	
	private int getTextHeight() {
		return 3 * font.fontHeight + 2 * TEXT_SPACING;
	}
	
	public void syncWorldTime(long worldTime) {
		currentTime = worldTime;
		
		updateText();
	}
	
	public void syncWorld(String worldName) {
		currentWorldName = worldName;
		
		updateText();
	}
	
	public void syncTask(Task task) {
		currentTask = task;
		
		updateText();
	}
}

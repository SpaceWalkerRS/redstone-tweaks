package redstonetweaks.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import redstonetweaks.gui.widget.RTButtonWidget;

public abstract class RTWindow extends RTAbstractParentElement implements RTElement {
	
	private static final Identifier TEXTURE = new Identifier("textures/gui/demo_background.png");
	private static final int TEXTURE_WIDTH = 248;
	private static final int TEXTURE_HEIGHT = 166;
	private static final int TEXTURE_BORDER = 4;
	private static final int TITLE_MARGIN = 10;
	protected static final int TEXT_COLOR = 16777215;
	
	public final RTMenuScreen screen;
	private final Text title;
	private final List<RTElement> contents;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private int headerHeight;
	
	private RTButtonWidget closeButton;
	
	public RTWindow(RTMenuScreen screen, Text title, int x, int y, int width, int height) {
		this.screen = screen;
		this.title = title;
		this.contents = new ArrayList<>();
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public List<? extends Element> children() {
		return contents;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void allowHover(boolean allowHover) {
		contents.forEach((element) -> element.allowHover(allowHover));
	}
	
	public Text getTitle() {
		return title;
	}
	
	public void init() {
		contents.clear();
		
		closeButton = new RTButtonWidget(x + 5, y + 5, 20, 20, () -> new TranslatableText("x"), (button) -> {
			close();
		});
		contents.add(closeButton);
		
		setHeaderHeight(TITLE_MARGIN + 30);
		
		initContents();
	}

	protected abstract void initContents();

	public void tick() {
		tickContents();
	}

	protected abstract void tickContents();
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawBackgroundTextureBelow(matrices, getY(), mouseX, mouseY, delta);
		
		drawCenteredText(matrices, screen.getTextRenderer(), getTitle(), getX() + getWidth() / 2, getY() + TITLE_MARGIN, 16777215);
		closeButton.render(matrices, mouseX, mouseY, delta);
		renderContents(matrices, mouseX, mouseY, delta);
	}
	
	protected void drawBackgroundTextureBelow(MatrixStack matrices, int cuttoffY, int mouseX, int mouseY, float delta) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		screen.client.getTextureManager().bindTexture(TEXTURE);
		
		int height = Math.min(getHeight(), getY() + getHeight() - cuttoffY);
		while (height > 0) {
			int width = getWidth();
			
			int drawY = y + getHeight() - height;
			int textureY = height == getHeight() ? 0 : (height == TEXTURE_BORDER ? TEXTURE_HEIGHT - TEXTURE_BORDER : TEXTURE_BORDER);
			int textureHeight = Math.max(Math.min(height - TEXTURE_BORDER, TEXTURE_HEIGHT - 2 * TEXTURE_BORDER), TEXTURE_BORDER);
			
			while (width > 0) {
				int drawX = x + getWidth() - width;
				int textureX = width == getWidth() ? 0 : (width == TEXTURE_BORDER ? TEXTURE_WIDTH - TEXTURE_BORDER : TEXTURE_BORDER);
				int textureWidth = Math.max(Math.min(width - TEXTURE_BORDER, TEXTURE_WIDTH - 2 * TEXTURE_BORDER), TEXTURE_BORDER);
				
				drawTexture(matrices, drawX, drawY, textureX, textureY, textureWidth, textureHeight);
				
				width -= textureWidth;
			}
			
			height -= textureHeight;
		}
	}
	
	protected abstract void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta);
	
	protected void addChild(RTElement child) {
		contents.add(child);
	}
	
	public int getHeaderHeight() {
		return headerHeight;
	}
	
	protected void setHeaderHeight(int newHeight) {
		headerHeight = newHeight;
	}
	
	public void close() {
		screen.closeWindow(this);
	}
}

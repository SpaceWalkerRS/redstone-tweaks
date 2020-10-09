package redstonetweaks.gui.widget;

import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class RTTexturedButtonWidget extends RTButtonWidget {
	
	private final Identifier texture;
	private final int textureX;
	private final int textureY;
	private final int textureWidth;
	private final int textureHeight;
	private final int hoveredTextureXOffset;
	
	public RTTexturedButtonWidget(int x, int y, int width, int height, Identifier texture, int textureX, int textureY, int textureWidth, int textureHeight, int hoveredVOffset, PressAction onPress) {
		this(x, y, width, height, texture, textureX, textureY, textureWidth, textureHeight, hoveredVOffset, () -> new TranslatableText(""), onPress, EMPTY);
	}
	
	public RTTexturedButtonWidget(int x, int y, int width, int height, Identifier texture, int textureX, int textureY, int textureWidth, int textureHeight, int hoveredVOffset, Supplier<Text> messageSupplier, PressAction onPress, TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, messageSupplier, onPress, tooltipSupplier);
		
		this.texture = texture;
		this.textureX = textureX;
		this.textureY = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.hoveredTextureXOffset = hoveredVOffset;
	}
	
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		int textureY = this.textureY;
		if (isHovered()) {
			textureY += hoveredTextureXOffset;
		}
		
		drawTexture(matrices, getX(), getY(), textureX, textureY, getWidth(), getHeight(), textureWidth, textureHeight);
	}
}

package redstonetweaks.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import redstonetweaks.gui.widget.RTTextFieldWidget;
import redstonetweaks.util.RTMathHelper;

public abstract class RTListWidget<E extends RTListWidget.Entry<E>> extends ElementListWidget<E> implements RTElement {
	
	protected static final int SCROLLBAR_WIDTH = 6;
	protected static final int TEXT_COLOR = 16777215;
	private static final Map<String, Double> SAVED_SCROLL_AMOUNTS = new HashMap<>();
	
	public final RTMenuScreen screen;
	private final int rowWidth;
	
	protected List<Text> currentTooltip;
	private boolean scrolling;
	private int entryTitleWidth = 0;
	private String savedScrollAmountKey;

	public RTListWidget(RTMenuScreen screen, int x, int y, int width, int height, int entryHeight, String savedScrollAmountKey) {
		super(screen.client, width, height, y, y + height, entryHeight);
		setLeftPos(x);
		
		this.screen = screen;
		this.rowWidth = width - 10;
		
		this.savedScrollAmountKey = savedScrollAmountKey;
	}
	
	@Override
	public int getRowWidth() {
		return rowWidth;
	}
	
	@Override
	protected int getScrollbarPositionX() {
		return getX() + getWidth() - SCROLLBAR_WIDTH - 2;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean clicked = mouseClick(mouseX, mouseY, button);
		
		unfocusTextFields(clicked ? getFocused() : null);
		
		return clicked;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		currentTooltip = null;
		
		renderList(matrices, mouseX, mouseY, delta);
		if (getMaximumScroll() > 0) {
			renderScrollbar(matrices);
		}
		
		if (currentTooltip != null) {
			screen.renderTooltip(matrices, currentTooltip, mouseX, mouseY);
		}
	}
	
	@Override
	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		scrolling = button == 0 && mouseX >= getScrollbarPositionX() && mouseX < getScrollbarPositionX() + SCROLLBAR_WIDTH;
		super.updateScrollingState(mouseX, mouseY, button);
	}
	
	@Override
	protected int getRowTop(int index) {
		return getY() + 4 - roundedScrollAmount() + index * itemHeight;
	}
	
	@Override
	public int getX() {
		return left;
	}
	
	@Override
	public int getY() {
		return top;
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
		children().forEach((element) -> element.allowHover(allowHover));
	}
	
	public void init() {
		clearEntries();
		entryTitleWidth = 0;
		
		initList();
		initEntries();
		
		setScrollAmount(SAVED_SCROLL_AMOUNTS.getOrDefault(savedScrollAmountKey, 0.0D));
	}
	
	protected abstract void initList();
	
	private void initEntries() {
		for (Entry<E> e : children()) {
			e.init(getEntryTitleWidth());
		}
	}
	
	public boolean focusedIsTextField() {
		E focused = getFocused();
		
		if (focused == null) {
			return false;
		} else {
			return focused.focusedIsTextField();
		}
	}
	
	private boolean mouseClick(double mouseX, double mouseY, int button) {
		updateScrollingState(mouseX, mouseY, button);
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			E entry = getEntryAtPos(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					setFocused(entry);
					setDragging(true);
					
					return true;
				}
			} else if (button == 0) {
				clickedHeader((int)(mouseX - (getX() + getWidth() / 2 - getRowWidth() / 2)), (int)(mouseY - getY()) + roundedScrollAmount() - 4);
				
				return true;
			}

			return scrolling;
		}
	}
	
	private void renderScrollbar(MatrixStack matrices) {
		int left = getScrollbarPositionX();
		int right = left + SCROLLBAR_WIDTH;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		int maxScroll = getMaximumScroll();
		
		int length = MathHelper.clamp((getHeight() * getHeight()) / getMaxPosition(), 32, getHeight() - 8);
		int bar_top = (int)(getScrollAmount() / maxScroll * (getHeight() - length)) + getY();
		if (bar_top < getY()) {
			bar_top = getY();
		}
		
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		
		bufferBuilder.vertex(left     , getY() + getHeight(), 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(right    , getY() + getHeight(), 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(right    , getY()              , 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(left     , getY()              , 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 200).next();
		
		bufferBuilder.vertex(left     , bar_top + length    , 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 200).next();
		bufferBuilder.vertex(right    , bar_top + length    , 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 200).next();
		bufferBuilder.vertex(right    , bar_top             , 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 200).next();
		bufferBuilder.vertex(left     , bar_top             , 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 200).next();
		
		bufferBuilder.vertex(left     , bar_top + length - 1, 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 200).next();
		bufferBuilder.vertex(right - 1, bar_top + length - 1, 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 200).next();
		bufferBuilder.vertex(right - 1, bar_top             , 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 200).next();
		bufferBuilder.vertex(left     , bar_top             , 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 200).next();
		
		tessellator.draw();
		
		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
	}
	
	private void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int itemCount = getItemCount();

		for (int index = 0; index < itemCount; ++index) {
			int rowTop = getRowTop(index);
			
			if (rowTop >= getY() && rowTop <= getY() + getHeight()) {
				int rowWidth = getRowWidth();
				int rowLeft = getRowLeft();
				
				E entry = getEntry(index);
				boolean hovered = isMouseOver(mouseX, mouseY) && Objects.equals(getEntryAtPos(mouseX, mouseY), entry);
				
				entry.render(matrices, index, rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, hovered, delta);
			}
		}
	}
	
	private E getEntryAtPos(double x, double y) {
		int halfWidth = getRowWidth() / 2;
		int centerX = getX() + getWidth() / 2;
		int left = centerX - halfWidth;
		int right = centerX + halfWidth;
		int index = (int)Math.floor((y - getY() - 4 + roundedScrollAmount()) / itemHeight);
		return x < getScrollbarPositionX() && x >= left && x <= right && index >= 0 && index < getItemCount() ? children().get(index) : null;
	}
	
	private int roundedScrollAmount() {
		return RTMathHelper.roundToMultiple(getScrollAmount(), itemHeight);
	}
	
	// This method is private in EntryListWidget.class
	private int getMaximumScroll() {
		return Math.max(0, getMaxPosition() - (getHeight() - 4));
	}
	
	public void saveScrollAmount() {
		SAVED_SCROLL_AMOUNTS.put(savedScrollAmountKey, getScrollAmount());
	}
	
	public void tick() {
		for (E entry : children()) {
			entry.tick();
		}
	}
	
	public void unfocusTextFields(Entry<E> except) {
		for (Entry<E> entry : children()) {
			if (entry != except) {
				entry.unfocusTextFields();
			}
		}
	}
	
	public void filter(String query) {
		clearEntries();
		
		filterEntries(query.toLowerCase());
		initEntries();
		
		setScrollAmount(getScrollAmount());
	}
	
	protected abstract void filterEntries(String query);
	
	protected int getEntryTitleWidth() {
		return entryTitleWidth;
	}
	
	protected void updateEntryTitleWidth(int width) {
		entryTitleWidth = Math.max(entryTitleWidth, width + 15);
	}
	
	protected void resetEntryTitleWidth() {
		entryTitleWidth = 0;
	}
	
	public static abstract class Entry<E extends RTListWidget.Entry<E>> extends ElementListWidget.Entry<E> {
		
		@Override
		public List<? extends Element> children() {
			return getChildren();
		}
		
		public abstract List<? extends RTElement> getChildren();
		
		public void init(int titleWidth) {
			
		}
		
		public void allowHover(boolean allowHover) {
			getChildren().forEach((element) -> element.allowHover(allowHover));
		}
		
		public abstract void tick();
		
		protected void unfocusTextFields() {
			
		}
		
		public boolean focusedIsTextField() {
			if (getFocused() instanceof RTTextFieldWidget && ((RTTextFieldWidget)getFocused()).isActive()) {
				return true;
			}
			return hasFocusedTextField();
		}
		
		protected abstract boolean hasFocusedTextField();
	}
}

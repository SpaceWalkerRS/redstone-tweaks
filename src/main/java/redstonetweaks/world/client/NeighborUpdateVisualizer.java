package redstonetweaks.world.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import redstonetweaks.world.common.UpdateType;

public class NeighborUpdateVisualizer {
	
	private static final float BLOCK_UPDATE_RED = 1.0F;
	private static final float BLOCK_UPDATE_GREEN = 0.9F;
	private static final float BLOCK_UPDATE_BLUE = 0.4F;
	private static final float BLOCK_UPDATE_ALPHA = 0.4F;
	
	private static final float COMPARATOR_UPDATE_RED = 1.0F;
	private static final float COMPARATOR_UPDATE_GREEN = 0.0F;
	private static final float COMPARATOR_UPDATE_BLUE = 0.0F;
	private static final float COMPARATOR_UPDATE_ALPHA = 0.4F;
	
	private static final float SHAPE_UPDATE_RED = 0.2F;
	private static final float SHAPE_UPDATE_GREEN = 0.3F;
	private static final float SHAPE_UPDATE_BLUE = 1.0F;
	private static final float SHAPE_UPDATE_ALPHA = 0.4F;
	
	private static final float NOTIFIER_RED = 1.0F;
	private static final float NOTIFIER_GREEN = 1.0F;
	private static final float NOTIFIER_BLUE = 1.0F;
	private static final float NOTIFIER_ALPHA = 0.4F;
	
	private static final float SOURCE_RED = 1.0F;
	private static final float SOURCE_GREEN = 1.0F;
	private static final float SOURCE_BLUE = 1.0F;
	private static final float SOURCE_ALPHA = 0.4F;
	
	private final MinecraftClient client;
	
	private BlockPos pos = null;
	private BlockPos notifierPos = null;
	private BlockPos sourcePos = null;
	private UpdateType updateType = UpdateType.NONE;
	
	public NeighborUpdateVisualizer(MinecraftClient client) {
		this.client = client;
	}
	
	public void draw(MatrixStack matrices) {
		RenderSystem.disableTexture();
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();

		matrices.push();

		Camera camera = client.gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

		if (sourcePos != null) {
			drawFancyBoxOutline(builder, matrices, sourcePos, SOURCE_RED, SOURCE_GREEN, SOURCE_BLUE, SOURCE_ALPHA);
		}
		
		tessellator.draw();
		
		RenderSystem.enableCull();
		
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		
		if (notifierPos != null) {
			drawBox(builder, matrices, notifierPos, NOTIFIER_RED, NOTIFIER_GREEN, NOTIFIER_BLUE, NOTIFIER_ALPHA);
		}
		if (pos != null) {
			drawBox(builder, matrices, pos, getRed(), getGreen(), getBlue(), getAlpha());
		}
		
		tessellator.draw();

		matrices.pop();
		
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
	}
	
	private void drawFancyBoxOutline(BufferBuilder builder, MatrixStack matrices, BlockPos pos, float r, float g, float b, float a) {
		Matrix4f model = matrices.peek().getModel();

		float x0 = pos.getX();
		float y0 = pos.getY();
		float z0 = pos.getZ();

		float x1 = pos.getX() + 1;
		float y1 = pos.getY() + 1;
		float z1 = pos.getZ() + 1;
		
		float l = 0.3f;
		float d = 0.02f;
		
		// Back face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + d, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + d, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y0, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + d, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + d, z1 - l).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + l, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + l, z1 - d).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1 - l, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - l, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z1 - d).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1 - d, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1 - l).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1 - d, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1 - l, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - l, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + d, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + l, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + l, z0).color(r, g, b, a).next();
		
		// Front face
		builder.vertex(model, x1, y0 + d, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + l, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + l, z0 + d).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y1 - l, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - l, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0 + d).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y1 - d, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - d, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0 + l).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y1 - d, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - d, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1 - d).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y1 - l, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - l, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y0 + d, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + l, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + l, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y0, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z0 + l).color(r, g, b, a).next();
		
		// Right face
		builder.vertex(model, x0 + d, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + l, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0 + l, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0 + d, y1 - l, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - l, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1 - d, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0 + l, y1 - d, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1 - d, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y1 - d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y1 - d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y1 - l, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1 - l, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0 + l, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + l, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + d, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0 + l, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0 + d, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0 + d, z0).color(r, g, b, a).next();
		
		// Left face
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0 + d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + d, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - l, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0 + d, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y0 + d, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0 + l, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0 + l, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y1 - l, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - l, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1 - d, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - l, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y1, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x0 + d, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1 - d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1 - l, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1 - l, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y0 + d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0 + d, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0 + l, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0 + l, z1).color(r, g, b, a).next();
		
		// Bottom face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z0 + d).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - l, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y0, z0 + d).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0, z0 + l).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y0, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y0, z1 - d).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - l, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y0, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x0 + d, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y0, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y0, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y0, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z0 + l).color(r, g, b, a).next();
		
		// Top face
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z1 - l).color(r, g, b, a).next();
		
		builder.vertex(model, x0, y1, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1, z1 - d).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - l, y1, z1 - d).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1, z1 - d).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y1, z1 - l).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1 - l).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - d, y1, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1 - d, y1, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0 + l).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0 + d).color(r, g, b, a).next();
		
		builder.vertex(model, x1 - l, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1 - l, y1, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		
		builder.vertex(model, x0 + d, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x0 + d, y1, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1, z0 + d).color(r, g, b, a).next();
		builder.vertex(model, x0 + l, y1, z0).color(r, g, b, a).next();
	}
	
	private void drawBox(BufferBuilder builder, MatrixStack matrices, BlockPos pos, float r, float g, float b, float a) {
		Matrix4f model = matrices.peek().getModel();

		float x0 = pos.getX();
		float y0 = pos.getY();
		float z0 = pos.getZ();

		float x1 = pos.getX() + 1;
		float y1 = pos.getY() + 1;
		float z1 = pos.getZ() + 1;

		// Back Face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();

		// Front Face
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();

		// Right Face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();

		// Left Face
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();

		// Bottom Face
		builder.vertex(model, x0, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z0).color(r, g, b, a).next();
		builder.vertex(model, x1, y0, z1).color(r, g, b, a).next();
		builder.vertex(model, x0, y0, z1).color(r, g, b, a).next();

		// Top Face
		builder.vertex(model, x0, y1, z0).color(r, g, b, a).next();
		builder.vertex(model, x0, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z1).color(r, g, b, a).next();
		builder.vertex(model, x1, y1, z0).color(r, g, b, a).next();
	}
	
	private float getRed() {
		switch (updateType) {
		case BLOCK_UPDATE:
			return BLOCK_UPDATE_RED;
		case COMPARATOR_UPDATE:
			return COMPARATOR_UPDATE_RED;
		case SHAPE_UPDATE:
			return SHAPE_UPDATE_RED;
		default:
			return 0.0F;
		}
	}
	
	private float getGreen() {
		switch (updateType) {
		case BLOCK_UPDATE:
			return BLOCK_UPDATE_GREEN;
		case COMPARATOR_UPDATE:
			return COMPARATOR_UPDATE_GREEN;
		case SHAPE_UPDATE:
			return SHAPE_UPDATE_GREEN;
		default:
			return 0.0F;
		}
	}
	
	private float getBlue() {
		switch (updateType) {
		case BLOCK_UPDATE:
			return BLOCK_UPDATE_BLUE;
		case COMPARATOR_UPDATE:
			return COMPARATOR_UPDATE_BLUE;
		case SHAPE_UPDATE:
			return SHAPE_UPDATE_BLUE;
		default:
			return 0.0F;
		}
	}
	
	private float getAlpha() {
		switch (updateType) {
		case BLOCK_UPDATE:
			return BLOCK_UPDATE_ALPHA;
		case COMPARATOR_UPDATE:
			return COMPARATOR_UPDATE_ALPHA;
		case SHAPE_UPDATE:
			return SHAPE_UPDATE_ALPHA;
		default:
			return 0.0F;
		}
	}
	
	public void updateBoxPositions(BlockPos pos, BlockPos notifierPos, BlockPos sourcePos, UpdateType updateType) {
		this.pos = pos;
		this.notifierPos = notifierPos;
		this.sourcePos = sourcePos;
		this.updateType = updateType;
	}
	
	public void onDisconnect() {
		updateBoxPositions(null, null, null, UpdateType.NONE);
	}
}

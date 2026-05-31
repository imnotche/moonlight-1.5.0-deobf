package me.twerknation28.moonlight.util;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import java.awt.Color;
import net.minecraft.util.math.Box;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.Tessellator;
import java.util.function.Supplier;
import net.minecraft.client.render.GameRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;

public class RenderUtil implements Util
{
    public static void rect(final MatrixStack stack, final float x1, final float y1, final float x2, final float y2, final int color) {
        rectFilled(stack, x1, y1, x2, y2, color);
    }
    
    public static void rect(final MatrixStack stack, final float x1, final float y1, final float x2, final float y2, final int color, final float width) {
        drawHorizontalLine(stack, x1, x2, y1, color, width);
        drawVerticalLine(stack, x2, y1, y2, color, width);
        drawHorizontalLine(stack, x1, x2, y2, color, width);
        drawVerticalLine(stack, x1, y1, y2, color, width);
    }
    
    protected static void drawHorizontalLine(final MatrixStack matrices, float x1, float x2, final float y, final int color) {
        if (x2 < x1) {
            final float i = x1;
            x1 = x2;
            x2 = i;
        }
        rectFilled(matrices, x1, y, x2 + 1.0f, y + 1.0f, color);
    }
    
    protected static void drawVerticalLine(final MatrixStack matrices, final float x, float y1, float y2, final int color) {
        if (y2 < y1) {
            final float i = y1;
            y1 = y2;
            y2 = i;
        }
        rectFilled(matrices, x, y1 + 1.0f, x + 1.0f, y2, color);
    }
    
    protected static void drawHorizontalLine(final MatrixStack matrices, float x1, float x2, final float y, final int color, final float width) {
        if (x2 < x1) {
            final float i = x1;
            x1 = x2;
            x2 = i;
        }
        rectFilled(matrices, x1, y, x2 + width, y + width, color);
    }
    
    protected static void drawVerticalLine(final MatrixStack matrices, final float x, float y1, float y2, final int color, final float width) {
        if (y2 < y1) {
            final float i = y1;
            y1 = y2;
            y2 = i;
        }
        rectFilled(matrices, x, y1 + width, x + width, y2, color);
    }
    
    public static void rectFilled(final MatrixStack matrix, float x1, float y1, float x2, float y2, final int color) {
        if (x1 < x2) {
            final float i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            final float i = y1;
            y1 = y2;
            y2 = i;
        }
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float g = (color >> 16 & 0xFF) / 255.0f;
        final float h = (color >> 8 & 0xFF) / 255.0f;
        final float j = (color & 0xFF) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        final BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1, y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x2, y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x2, y1, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1, y1, 0.0f).color(g, h, j, f);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
    
    public static void drawBoxFilled(final MatrixStack stack, final Box box, final Color c) {
        final float minX = (float)(box.minX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        final float minY = (float)(box.minY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        final float minZ = (float)(box.minZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        final float maxX = (float)(box.maxX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        final float maxY = (float)(box.maxY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        final float maxZ = (float)(box.maxZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        final Tessellator tessellator = Tessellator.getInstance();
        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        final BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        clean3D();
    }
    
    public static void drawBoxFilled(final MatrixStack stack, final Vec3d vec, final Color c) {
        drawBoxFilled(stack, Box.from(vec), c);
    }
    
    public static void drawBoxFilled(final MatrixStack stack, final BlockPos bp, final Color c) {
        drawBoxFilled(stack, new Box(bp), c);
    }
    
    public static void drawBox(final MatrixStack stack, final Box box, final Color c, final double lineWidth) {
        final float minX = (float)(box.minX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        final float minY = (float)(box.minY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        final float minZ = (float)(box.minZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        final float maxX = (float)(box.maxX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        final float maxY = (float)(box.maxY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        final float maxZ = (float)(box.maxZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        setup3D();
        RenderSystem.lineWidth((float)lineWidth);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.defaultBlendFunc();
        final BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        WorldRenderer.drawBox(stack, (VertexConsumer)bufferBuilder, (double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        clean3D();
    }
    
    public static void drawBox(final MatrixStack stack, final Vec3d vec, final Color c, final double lineWidth) {
        drawBox(stack, Box.from(vec), c, lineWidth);
    }
    
    public static void drawBox(final MatrixStack stack, final BlockPos bp, final Color c, final double lineWidth) {
        drawBox(stack, new Box(bp), c, lineWidth);
    }
    
    public static MatrixStack matrixFrom(final Vec3d pos) {
        final MatrixStack matrices = new MatrixStack();
        final Camera camera = RenderUtil.mc.gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(pos.getX() - camera.getPos().x, pos.getY() - camera.getPos().y, pos.getZ() - camera.getPos().z);
        return matrices;
    }
    
    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
    
    public static void setup3D() {
        setup();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }
    
    public static void clean() {
        RenderSystem.disableBlend();
    }
    
    public static void clean3D() {
        clean();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
    
    public static double roundSliderForConfig(final double val) {
        return Double.parseDouble(MathUtil.getRounded(val));
    }
    
    public static float roundSliderStep(final float input, final float step) {
        return Math.round(input / step) * step;
    }
    
    public static float reCheckSliderRange(final float value, final float min, final float max) {
        return Math.min(Math.max(value, min), max);
    }
}

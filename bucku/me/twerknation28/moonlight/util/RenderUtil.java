package me.twerknation28.moonlight.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.twerknation28.moonlight.util.MathUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class RenderUtil
implements Util {
    public static void rect(MatrixStack stack, float x1, float y1, float x2, float y2, int color) {
        RenderUtil.rectFilled(stack, x1, y1, x2, y2, color);
    }

    public static void rect(MatrixStack stack, float x1, float y1, float x2, float y2, int color, float width) {
        RenderUtil.drawHorizontalLine(stack, x1, x2, y1, color, width);
        RenderUtil.drawVerticalLine(stack, x2, y1, y2, color, width);
        RenderUtil.drawHorizontalLine(stack, x1, x2, y2, color, width);
        RenderUtil.drawVerticalLine(stack, x1, y1, y2, color, width);
    }

    protected static void drawHorizontalLine(MatrixStack matrices, float x1, float x2, float y, int color) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }
        RenderUtil.rectFilled(matrices, x1, y, x2 + 1.0f, y + 1.0f, color);
    }

    protected static void drawVerticalLine(MatrixStack matrices, float x, float y1, float y2, int color) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }
        RenderUtil.rectFilled(matrices, x, y1 + 1.0f, x + 1.0f, y2, color);
    }

    protected static void drawHorizontalLine(MatrixStack matrices, float x1, float x2, float y, int color, float width) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }
        RenderUtil.rectFilled(matrices, x1, y, x2 + width, y + width, color);
    }

    protected static void drawVerticalLine(MatrixStack matrices, float x, float y1, float y2, int color, float width) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }
        RenderUtil.rectFilled(matrices, x, y1 + width, x + width, y2, color);
    }

    public static void rectFilled(MatrixStack matrix, float x1, float y1, float x2, float y2, int color) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f;
        float j = (float)(color & 0xFF) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::method_34540);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1, y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x2, y2, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x2, y1, 0.0f).color(g, h, j, f);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1, y1, 0.0f).color(g, h, j, f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawBoxFilled(MatrixStack stack, Box box, Color c) {
        float minX = (float)(box.minX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float)(box.minY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float)(box.minZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float)(box.maxX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float)(box.maxY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float)(box.maxZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        Tessellator tessellator = Tessellator.getInstance();
        RenderUtil.setup3D();
        RenderSystem.setShader(GameRenderer::method_34540);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
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
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        RenderUtil.clean3D();
    }

    public static void drawBoxFilled(MatrixStack stack, Vec3d vec, Color c) {
        RenderUtil.drawBoxFilled(stack, Box.from((Vec3d)vec), c);
    }

    public static void drawBoxFilled(MatrixStack stack, BlockPos bp, Color c) {
        RenderUtil.drawBoxFilled(stack, new Box(bp), c);
    }

    public static void drawBox(MatrixStack stack, Box box, Color c, double lineWidth) {
        float minX = (float)(box.minX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float)(box.minY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float)(box.minZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float)(box.maxX - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float)(box.maxY - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float)(box.maxZ - RenderUtil.mc.getEntityRenderDispatcher().camera.getPos().getZ());
        RenderUtil.setup3D();
        RenderSystem.lineWidth((float)((float)lineWidth));
        RenderSystem.setShader(GameRenderer::method_34535);
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        WorldRenderer.drawBox((MatrixStack)stack, (VertexConsumer)bufferBuilder, (double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ, (float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)((float)c.getAlpha() / 255.0f));
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)bufferBuilder.end());
        RenderUtil.clean3D();
    }

    public static void drawBox(MatrixStack stack, Vec3d vec, Color c, double lineWidth) {
        RenderUtil.drawBox(stack, Box.from((Vec3d)vec), c, lineWidth);
    }

    public static void drawBox(MatrixStack stack, BlockPos bp, Color c, double lineWidth) {
        RenderUtil.drawBox(stack, new Box(bp), c, lineWidth);
    }

    public static MatrixStack matrixFrom(Vec3d pos) {
        MatrixStack matrices = new MatrixStack();
        Camera camera = RenderUtil.mc.gameRenderer.getCamera();
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
        RenderUtil.setup();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableCull();
    }

    public static void clean() {
        RenderSystem.disableBlend();
    }

    public static void clean3D() {
        RenderUtil.clean();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableCull();
    }

    public static double roundSliderForConfig(double val) {
        return Double.parseDouble(MathUtil.getRounded(val));
    }

    public static float roundSliderStep(float input, float step) {
        return (float)Math.round(input / step) * step;
    }

    public static float reCheckSliderRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}

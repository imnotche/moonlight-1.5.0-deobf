package me.twerknation28.moonlight.features.modules.render;

import me.twerknation28.moonlight.util.RenderUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import java.awt.Color;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.event.EventListener;
import net.minecraft.util.math.BlockPos;
import me.twerknation28.moonlight.util.DirectionUtil;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class ProneESP extends Module
{
    public final Setting<Integer> boxAlpha;
    public final Setting<Integer> lineAlpha;
    public final Setting<Double> lineWidth;
    public final Setting<Double> fadeDistance;
    public Setting<Boolean> whileAirborne;
    public Setting<boxMode> renderMode;
    
    public ProneESP() {
        super("ProneESP", "Shows where to phase out of crawl", Category.RENDER, true, false, false);
        this.boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 15, 0, 255, 1));
        this.lineAlpha = this.register(new Setting<Integer>("LineAlpha", 50, 0, 255, 1));
        this.lineWidth = this.register(new Setting<Double>("LineWidth", 2.0, 0.1, 4.0, 0.1));
        this.fadeDistance = this.register(new Setting<Double>("FadeDist", 0.5, 0.0, 1.0, 0.1));
        this.whileAirborne = this.register(new Setting<Boolean>("WhileAirborne", false));
        this.renderMode = this.register(new Setting<boxMode>("RenderMode", boxMode.TOP));
    }
    
    @EventListener
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (ProneESP.mc.player != null && ProneESP.mc.world != null && ProneESP.mc.player.isInSwimmingPose()) {
            if (!ProneESP.mc.player.isOnGround() && !this.whileAirborne.getValue()) {
                return;
            }
            final BlockPos playerPos = ProneESP.mc.player.getBlockPos();
            for (final DirectionUtil.EightWayDirections direction : DirectionUtil.EightWayDirections.values()) {
                if (DirectionUtil.isCardinal(direction)) {
                    final BlockPos blockPos = direction.offset(playerPos);
                    this.proneESPRender(blockPos, event, direction);
                }
            }
        }
    }
    
    private void proneESPRender(final BlockPos blockPos, final Render3DEvent event, final DirectionUtil.EightWayDirections direction) {
        if (!ProneESP.mc.world.getBlockState(blockPos).isReplaceable()) {
            final BlockState state = ProneESP.mc.world.getBlockState(blockPos.up());
            Color color = null;
            if (!state.isReplaceable()) {
                return;
            }
            color = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.boxAlpha.getValue());
            final BlockPos playerPos = ProneESP.mc.player.getBlockPos();
            final Vec3d pos = ProneESP.mc.player.getPos();
            final double dx = pos.getX() - playerPos.getX();
            final double dz = pos.getZ() - playerPos.getZ();
            final double far = this.fadeDistance.getValue();
            final double near = 1.0 - this.fadeDistance.getValue();
            if (direction == DirectionUtil.EightWayDirections.EAST && dx >= far) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.WEST && dx <= near) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.SOUTH && dz >= far) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.NORTH && dz <= near) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.NORTHEAST && dz <= near && dx >= far) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.NORTHWEST && dz <= near && dx <= near) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.SOUTHEAST && dz >= far && dx >= far) {
                this.BoxRender(blockPos, event, color);
            }
            else if (direction == DirectionUtil.EightWayDirections.SOUTHWEST && dz >= far && dx <= near) {
                this.BoxRender(blockPos, event, color);
            }
        }
    }
    
    private void BoxRender(final BlockPos blockPos, final Render3DEvent event, final Color color) {
        final Box render1 = VoxelShapes.fullCube().getBoundingBox();
        final Color lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), this.lineAlpha.getValue());
        switch (this.renderMode.getValue().ordinal()) {
            case 0: {
                final Box render2 = new Box(blockPos.getX() + render1.minX, blockPos.getY() + render1.maxY, blockPos.getZ() + render1.minZ, blockPos.getX() + render1.maxX, blockPos.getY() + render1.maxY, blockPos.getZ() + render1.maxZ);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), render2, color);
                RenderUtil.drawBox(event.getMatrixStack(), render2, lineColor, this.lineWidth.getValue());
                break;
            }
            case 1: {
                final Box render2 = new Box(blockPos.getX() + render1.minX, blockPos.getY() + render1.minY, blockPos.getZ() + render1.minZ, blockPos.getX() + render1.maxX, blockPos.getY() + render1.minY, blockPos.getZ() + render1.maxZ);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), render2, color);
                RenderUtil.drawBox(event.getMatrixStack(), render2, lineColor, this.lineWidth.getValue());
                break;
            }
            case 2: {
                final Box render2 = new Box(blockPos.getX() + render1.minX, blockPos.getY() + render1.minY, blockPos.getZ() + render1.minZ, blockPos.getX() + render1.maxX, blockPos.getY() + render1.maxY, blockPos.getZ() + render1.maxZ);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), render2, color);
                RenderUtil.drawBox(event.getMatrixStack(), render2, lineColor, this.lineWidth.getValue());
                break;
            }
        }
    }
    
    public enum boxMode
    {
        TOP, 
        BOTTOM, 
        BOX;
    }
}

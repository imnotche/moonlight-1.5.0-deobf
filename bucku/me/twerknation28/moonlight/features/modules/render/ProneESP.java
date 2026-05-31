package me.twerknation28.moonlight.features.modules.render;

import java.awt.Color;
import me.twerknation28.moonlight.event.EventListener;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.DirectionUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class ProneESP
extends Module {
    public final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 15, 0, 255, 1));
    public final Setting<Integer> lineAlpha = this.register(new Setting<Integer>("LineAlpha", 50, 0, 255, 1));
    public final Setting<Double> lineWidth = this.register(new Setting<Double>("LineWidth", 2.0, 0.1, 4.0, 0.1));
    public final Setting<Double> fadeDistance = this.register(new Setting<Double>("FadeDist", 0.5, 0.0, 1.0, 0.1));
    public Setting<Boolean> whileAirborne = this.register(new Setting<Boolean>("WhileAirborne", false));
    public Setting<boxMode> renderMode = this.register(new Setting<boxMode>("RenderMode", boxMode.TOP));

    public ProneESP() {
        super("ProneESP", "Shows where to phase out of crawl", Category.RENDER, true, false, false);
    }

    @Override
    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (ProneESP.mc.player != null && ProneESP.mc.world != null && ProneESP.mc.player.isInSwimmingPose()) {
            if (!ProneESP.mc.player.isOnGround() && !this.whileAirborne.getValue().booleanValue()) {
                return;
            }
            BlockPos playerPos = ProneESP.mc.player.getBlockPos();
            for (DirectionUtil.EightWayDirections direction : DirectionUtil.EightWayDirections.values()) {
                if (!DirectionUtil.isCardinal(direction).booleanValue()) continue;
                BlockPos blockPos = direction.offset(playerPos);
                this.proneESPRender(blockPos, event, direction);
            }
        }
    }

    private void proneESPRender(BlockPos blockPos, Render3DEvent event, DirectionUtil.EightWayDirections direction) {
        if (!ProneESP.mc.world.getBlockState(blockPos).isReplaceable()) {
            BlockState state = ProneESP.mc.world.getBlockState(blockPos.up());
            Color color = null;
            if (!state.isReplaceable()) {
                return;
            }
            color = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.boxAlpha.getValue());
            BlockPos playerPos = ProneESP.mc.player.getBlockPos();
            Vec3d pos = ProneESP.mc.player.getPos();
            double dx = pos.getX() - (double)playerPos.getX();
            double dz = pos.getZ() - (double)playerPos.getZ();
            double far = this.fadeDistance.getValue();
            double near = 1.0 - this.fadeDistance.getValue();
            if (direction == DirectionUtil.EightWayDirections.EAST && dx >= far) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.WEST && dx <= near) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.SOUTH && dz >= far) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.NORTH && dz <= near) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.NORTHEAST && dz <= near && dx >= far) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.NORTHWEST && dz <= near && dx <= near) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.SOUTHEAST && dz >= far && dx >= far) {
                this.BoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.SOUTHWEST && dz >= far && dx <= near) {
                this.BoxRender(blockPos, event, color);
            }
        }
    }

    private void BoxRender(BlockPos blockPos, Render3DEvent event, Color color) {
        Box render1 = VoxelShapes.fullCube().getBoundingBox();
        Color lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), this.lineAlpha.getValue());
        switch (this.renderMode.getValue().ordinal()) {
            case 0: {
                Box render = new Box((double)blockPos.getX() + render1.minX, (double)blockPos.getY() + render1.maxY, (double)blockPos.getZ() + render1.minZ, (double)blockPos.getX() + render1.maxX, (double)blockPos.getY() + render1.maxY, (double)blockPos.getZ() + render1.maxZ);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), render, color);
                RenderUtil.drawBox(event.getMatrixStack(), render, lineColor, (double)this.lineWidth.getValue());
                break;
            }
            case 1: {
                Box render = new Box((double)blockPos.getX() + render1.minX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.minZ, (double)blockPos.getX() + render1.maxX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.maxZ);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), render, color);
                RenderUtil.drawBox(event.getMatrixStack(), render, lineColor, (double)this.lineWidth.getValue());
                break;
            }
            case 2: {
                Box render = new Box((double)blockPos.getX() + render1.minX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.minZ, (double)blockPos.getX() + render1.maxX, (double)blockPos.getY() + render1.maxY, (double)blockPos.getZ() + render1.maxZ);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), render, color);
                RenderUtil.drawBox(event.getMatrixStack(), render, lineColor, (double)this.lineWidth.getValue());
            }
        }
    }

    public static enum boxMode {
        TOP,
        BOTTOM,
        BOX;

    }
}

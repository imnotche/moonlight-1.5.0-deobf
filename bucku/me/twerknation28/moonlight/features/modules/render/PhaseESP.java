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
import net.minecraft.world.BlockView;

public class PhaseESP
extends Module {
    public final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 15, 0, 255, 1));
    public final Setting<Integer> lineAlpha = this.register(new Setting<Integer>("LineAlpha", 50, 0, 255, 1));
    public final Setting<Double> lineWidth = this.register(new Setting<Double>("LineWidth", 2.0, 0.1, 4.0, 0.1));
    public Setting<Boolean> phaseRender = this.register(new Setting<Boolean>("PhaseESP", true));
    public final Setting<Boolean> newColor = this.register(new Setting<Boolean>("NewColour", Boolean.valueOf(false), v -> this.phaseRender.getValue()));
    public final Setting<Boolean> whileCrawl = this.register(new Setting<Boolean>("WhileCrawling", Boolean.valueOf(false), v -> this.phaseRender.getValue()));
    public final Setting<Double> fadeDistance = this.register(new Setting<Double>("FadeDist", Double.valueOf(0.5), Double.valueOf(0.0), Double.valueOf(1.0), Double.valueOf(0.1), v -> this.phaseRender.getValue()));
    public final Setting<Boolean> diagonal = this.register(new Setting<Boolean>("Diagonal", Boolean.valueOf(true), v -> this.phaseRender.getValue()));
    public Setting<Boolean> proneRender = this.register(new Setting<Boolean>("ProneESP", false));
    public Setting<Boolean> whileAirborne = this.register(new Setting<Boolean>("WhileAirborne", Boolean.valueOf(false), v -> this.proneRender.getValue()));
    public Setting<boxMode> renderMode = this.register(new Setting<boxMode>("RenderMode", boxMode.TOP, v -> this.proneRender.getValue()));

    public PhaseESP() {
        super("ESP", "Awareness Module 7000", Category.RENDER, true, false, false);
    }

    @Override
    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (PhaseESP.mc.player != null && PhaseESP.mc.world != null && PhaseESP.mc.player.isOnGround()) {
            BlockPos playerPos = PhaseESP.mc.player.getBlockPos();
            for (DirectionUtil.EightWayDirections direction : DirectionUtil.EightWayDirections.values()) {
                BlockPos blockPos;
                if ((this.diagonal.getValue().booleanValue() || DirectionUtil.isCardinal(direction).booleanValue()) && this.phaseRender.getValue().booleanValue() && (this.whileCrawl.getValue().booleanValue() || !PhaseESP.mc.player.isInSwimmingPose())) {
                    blockPos = direction.offset(playerPos);
                    this.phaseESPRender(blockPos, event, direction);
                }
                if (!DirectionUtil.isCardinal(direction).booleanValue() || !this.proneRender.getValue().booleanValue() || !PhaseESP.mc.player.isOnGround() && !this.whileAirborne.getValue().booleanValue() || !PhaseESP.mc.player.isInSwimmingPose()) continue;
                blockPos = direction.offset(playerPos);
                this.proneESPRender(blockPos, event, direction);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void phaseESPRender(BlockPos blockPos, Render3DEvent event, DirectionUtil.EightWayDirections direction) {
        if (PhaseESP.mc.world.getBlockState(blockPos).isReplaceable()) return;
        BlockState state = PhaseESP.mc.world.getBlockState(blockPos.down());
        Color color = null;
        if (this.newColor.getValue().booleanValue()) {
            if (!state.isReplaceable()) return;
            color = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.boxAlpha.getValue());
        } else {
            color = state.isReplaceable() ? new Color(255, 0, 0, this.boxAlpha.getValue()) : (state.getHardness((BlockView)PhaseESP.mc.world, blockPos.down()) < 0.0f ? new Color(0, 255, 0, this.boxAlpha.getValue()) : new Color(255, 255, 0, this.boxAlpha.getValue()));
        }
        BlockPos playerPos = PhaseESP.mc.player.getBlockPos();
        Vec3d pos = PhaseESP.mc.player.getPos();
        double dx = pos.getX() - (double)playerPos.getX();
        double dz = pos.getZ() - (double)playerPos.getZ();
        double far = this.fadeDistance.getValue();
        double near = 1.0 - this.fadeDistance.getValue();
        if (direction == DirectionUtil.EightWayDirections.EAST && dx >= far) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else if (direction == DirectionUtil.EightWayDirections.WEST && dx <= near) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else if (direction == DirectionUtil.EightWayDirections.SOUTH && dz >= far) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else if (direction == DirectionUtil.EightWayDirections.NORTH && dz <= near) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else if (direction == DirectionUtil.EightWayDirections.NORTHEAST && dz <= near && dx >= far) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else if (direction == DirectionUtil.EightWayDirections.NORTHWEST && dz <= near && dx <= near) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else if (direction == DirectionUtil.EightWayDirections.SOUTHEAST && dz >= far && dx >= far) {
            this.PhaseBoxRender(blockPos, event, color);
            return;
        } else {
            if (direction != DirectionUtil.EightWayDirections.SOUTHWEST || !(dz >= far) || !(dx <= near)) return;
            this.PhaseBoxRender(blockPos, event, color);
        }
    }

    private void proneESPRender(BlockPos blockPos, Render3DEvent event, DirectionUtil.EightWayDirections direction) {
        if (!PhaseESP.mc.world.getBlockState(blockPos).isReplaceable()) {
            BlockState state = PhaseESP.mc.world.getBlockState(blockPos.up());
            Color color = null;
            if (!state.isReplaceable()) {
                return;
            }
            color = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.boxAlpha.getValue());
            BlockPos playerPos = PhaseESP.mc.player.getBlockPos();
            Vec3d pos = PhaseESP.mc.player.getPos();
            double dx = pos.getX() - (double)playerPos.getX();
            double dz = pos.getZ() - (double)playerPos.getZ();
            double far = this.fadeDistance.getValue();
            double near = 1.0 - this.fadeDistance.getValue();
            if (direction == DirectionUtil.EightWayDirections.EAST && dx >= far) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.WEST && dx <= near) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.SOUTH && dz >= far) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.NORTH && dz <= near) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.NORTHEAST && dz <= near && dx >= far) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.NORTHWEST && dz <= near && dx <= near) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.SOUTHEAST && dz >= far && dx >= far) {
                this.ProneBoxRender(blockPos, event, color);
            } else if (direction == DirectionUtil.EightWayDirections.SOUTHWEST && dz >= far && dx <= near) {
                this.ProneBoxRender(blockPos, event, color);
            }
        }
    }

    private void PhaseBoxRender(BlockPos blockPos, Render3DEvent event, Color color) {
        Box render1 = VoxelShapes.fullCube().getBoundingBox();
        Box render = new Box((double)blockPos.getX() + render1.minX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.minZ, (double)blockPos.getX() + render1.maxX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.maxZ);
        RenderUtil.drawBoxFilled(event.getMatrixStack(), render, color);
        Color lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), this.lineAlpha.getValue());
        RenderUtil.drawBox(event.getMatrixStack(), render, lineColor, (double)this.lineWidth.getValue());
    }

    private void ProneBoxRender(BlockPos blockPos, Render3DEvent event, Color color) {
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

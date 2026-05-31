package me.twerknation28.moonlight.features.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.hit.HitResult;
import me.twerknation28.moonlight.util.RenderUtil;
import net.minecraft.world.BlockView;
import net.minecraft.util.hit.BlockHitResult;
import java.awt.Color;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class BlockHighlight extends Module
{
    public final Setting<Float> lineWidth;
    public final Setting<Integer> lineAlpha;
    public final Setting<Integer> boxAlpha;
    
    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block you're looking at", Category.RENDER, true, false, false);
        this.lineWidth = this.register(new Setting<Float>("Line Width", 1.0f, 0.1f, 3.0f, 0.1f));
        this.lineAlpha = this.register(new Setting<Integer>("Line Alpha", 255, 0, 255, 1));
        this.boxAlpha = this.register(new Setting<Integer>("Box Alpha", 150, 0, 255, 1));
    }
    
    @Subscribe
    @Override
    public void onRender3D(final Render3DEvent event) {
        final Color boxColor = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.boxAlpha.getValue());
        final Color lineColor = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.lineAlpha.getValue());
        final HitResult crosshairTarget = BlockHighlight.mc.crosshairTarget;
        if (crosshairTarget instanceof final BlockHitResult result) {
            final VoxelShape shape = BlockHighlight.mc.world.getBlockState(result.getBlockPos()).getOutlineShape((BlockView)BlockHighlight.mc.world, result.getBlockPos());
            if (shape.isEmpty()) {
                return;
            }
            Box box = shape.getBoundingBox();
            box = box.offset(result.getBlockPos());
            RenderUtil.drawBoxFilled(event.getMatrix(), box, boxColor);
            RenderUtil.drawBox(event.getMatrix(), box, lineColor, this.lineWidth.getValue());
        }
    }
}

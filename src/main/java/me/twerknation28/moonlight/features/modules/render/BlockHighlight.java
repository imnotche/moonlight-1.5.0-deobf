package me.twerknation28.moonlight.features.modules.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.RenderUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockHighlight
extends Module {
    public final Setting<Float> lineWidth = this.register(new Setting<Float>("Line Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f), Float.valueOf(0.1f)));
    public final Setting<Integer> lineAlpha = this.register(new Setting<Integer>("Line Alpha", 255, 0, 255, 1));
    public final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("Box Alpha", 150, 0, 255, 1));

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block you're looking at", Category.RENDER, true, false, false);
    }

    @Override
    @Subscribe
    public void onRender3D(Render3DEvent event) {
        Color boxColor = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.boxAlpha.getValue());
        Color lineColor = new Color(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), this.lineAlpha.getValue());
        HitResult hitResult = BlockHighlight.mc.crosshairTarget;
        if (hitResult instanceof BlockHitResult) {
            BlockHitResult result = (BlockHitResult)hitResult;
            VoxelShape shape = BlockHighlight.mc.world.getBlockState(result.getBlockPos()).getOutlineShape((BlockView)BlockHighlight.mc.world, result.getBlockPos());
            if (shape.isEmpty()) {
                return;
            }
            Box box = shape.getBoundingBox();
            box = box.offset(result.getBlockPos());
            RenderUtil.drawBoxFilled(event.getMatrix(), box, boxColor);
            RenderUtil.drawBox(event.getMatrix(), box, lineColor, (double)this.lineWidth.getValue().floatValue());
        }
    }
}

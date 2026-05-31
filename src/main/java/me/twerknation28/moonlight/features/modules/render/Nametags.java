package me.twerknation28.moonlight.features.modules.render;

import java.util.Iterator;
import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.RenderLabelEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;

public class Nametags extends Module
{
    public Nametags() {
        super("Nametags", "Shows additional information above the players", Category.RENDER, true, false, false);
    }
    
    private void render(final Render2DEvent event, final PlayerEntity entity) {
        final float distance = entity.distanceTo((Entity)Nametags.mc.player);
        final Camera camera = Nametags.mc.gameRenderer.getCamera();
        final Vec3d cameraPos = camera.getPos();
        final Vec3d entityPos = entity.getPos();
        final float size = 0.025f;
    }
    
    @Subscribe
    public void onRenderLabel(final RenderLabelEvent event) {
        if (this.isDisabled()) {
            return;
        }
        event.cancel();
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        for (final PlayerEntity player : Nametags.mc.world.getPlayers()) {
            if (player == Nametags.mc.player) {
                continue;
            }
            this.render(event, player);
        }
    }
}

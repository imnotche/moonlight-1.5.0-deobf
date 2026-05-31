package me.twerknation28.moonlight.features.modules.render;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.event.impl.RenderLabelEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Nametags
extends Module {
    public Nametags() {
        super("Nametags", "Shows additional information above the players", Category.RENDER, true, false, false);
    }

    private void render(Render2DEvent event, PlayerEntity entity) {
        float distance = entity.distanceTo((Entity)Nametags.mc.player);
        Camera camera = Nametags.mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        Vec3d entityPos = entity.getPos();
        float size = 0.025f;
    }

    @Subscribe
    public void onRenderLabel(RenderLabelEvent event) {
        if (this.isDisabled()) {
            return;
        }
        event.cancel();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        for (PlayerEntity player : Nametags.mc.world.getPlayers()) {
            if (player == Nametags.mc.player) continue;
            this.render(event, player);
        }
    }
}

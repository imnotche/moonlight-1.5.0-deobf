package me.twerknation28.moonlight.features.modules.player;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.event.impl.DisconnectEvent;
import me.twerknation28.moonlight.event.impl.PushEntityEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.util.FakePlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FakePlayer
extends Module {
    private FakePlayerEntity fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns fake player for testing", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (FakePlayer.mc.player != null && FakePlayer.mc.world != null) {
            this.fakePlayer = new FakePlayerEntity((PlayerEntity)FakePlayer.mc.player, Moonlight.NAME);
            this.fakePlayer.spawnPlayer();
        }
    }

    @Override
    public void onDisable() {
        if (this.fakePlayer != null) {
            this.fakePlayer.despawnPlayer();
            this.fakePlayer = null;
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        this.fakePlayer = null;
        this.disable();
    }

    @Subscribe
    public void onPushEntity(PushEntityEvent event) {
        if (!event.getPushed().equals(FakePlayer.mc.player) || event.getPusher().equals(this.fakePlayer)) {
            // empty if block
        }
    }
}

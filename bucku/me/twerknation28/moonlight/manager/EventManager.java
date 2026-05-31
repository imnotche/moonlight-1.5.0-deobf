package me.twerknation28.moonlight.manager;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.event.Stage;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.event.impl.ChatEvent;
import me.twerknation28.moonlight.event.impl.KeyEvent;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.event.impl.UpdateEvent;
import me.twerknation28.moonlight.event.impl.UpdateWalkingPlayerEvent;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.util.models.Timer;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EventManager
extends Feature {
    private final Timer logoutTimer = new Timer();
    private final Timer nameTimer = new Timer();
    String windowName = "moonlight 1.4.0-beta";
    String windowTitle = "";
    int titleCharCount = 1;
    boolean advancingTitle = true;

    public void init() {
        EVENT_BUS.register(this);
    }

    public void onUnload() {
        EVENT_BUS.unregister(this);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.getWindow().setTitle(this.windowTitle);
        if (this.nameTimer.passedS(0.25)) {
            if (this.titleCharCount >= this.windowName.length() && this.advancingTitle) {
                this.advancingTitle = false;
            } else if (this.titleCharCount <= 0 && !this.advancingTitle) {
                this.advancingTitle = true;
            }
            if (this.advancingTitle) {
                ++this.titleCharCount;
                if (this.titleCharCount >= this.windowName.length()) {
                    this.advancingTitle = false;
                }
            } else {
                --this.titleCharCount;
                if (this.titleCharCount <= 0) {
                    this.advancingTitle = true;
                }
            }
            this.windowTitle = this.windowName.substring(0, this.titleCharCount);
            this.nameTimer.reset();
        }
        if (!EventManager.fullNullCheck()) {
            Moonlight.moduleManager.onUpdate();
            Moonlight.moduleManager.sortModules(true);
            this.onTick();
        }
    }

    public void onTick() {
        if (EventManager.fullNullCheck()) {
            return;
        }
        Moonlight.moduleManager.onTick();
    }

    @Subscribe
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (EventManager.fullNullCheck()) {
            return;
        }
        if (event.getStage() == Stage.PRE) {
            Moonlight.speedManager.updateValues();
            Moonlight.rotationManager.updateRotations();
            Moonlight.positionManager.updatePosition();
        }
        if (event.getStage() == Stage.POST) {
            Moonlight.rotationManager.restoreRotations();
            Moonlight.positionManager.restorePosition();
        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        Moonlight.serverManager.onPacketReceived();
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            Moonlight.serverManager.update();
        }
    }

    @Subscribe
    public void onWorldRender(Render3DEvent event) {
        Moonlight.moduleManager.onRender3D(event);
    }

    @Subscribe
    public void onAttackBlock(AttackBlockEvent event) {
        Moonlight.moduleManager.AttackBlockEvent(event);
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        Moonlight.moduleManager.Send(event);
    }

    @Subscribe
    public void onRenderGameOverlayEvent(Render2DEvent event) {
        Moonlight.moduleManager.onRender2D(event);
    }

    @Subscribe
    public void onKeyInput(KeyEvent event) {
        Moonlight.moduleManager.onKeyPressed(event.getKey());
    }

    @Subscribe
    public void onChatSent(ChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.cancel();
            try {
                if (event.getMessage().length() > 1) {
                    Moonlight.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    String message = "Please enter a command.";
                    MutableText text = Text.literal((String)message);
                    EventManager.mc.inGameHud.getChatHud().addMessage((Text)text);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                String message = "An error occurred while running this command. Check the log!";
                MutableText text = Text.literal((String)(String.valueOf(Formatting.RED) + message));
                EventManager.mc.inGameHud.getChatHud().addMessage((Text)text);
            }
        }
    }
}

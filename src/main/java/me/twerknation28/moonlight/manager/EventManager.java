package me.twerknation28.moonlight.manager;

import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.event.impl.ChatEvent;
import me.twerknation28.moonlight.event.impl.KeyEvent;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.Stage;
import me.twerknation28.moonlight.event.impl.UpdateWalkingPlayerEvent;
import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.event.impl.UpdateEvent;
import me.twerknation28.moonlight.util.models.Timer;
import me.twerknation28.moonlight.features.Feature;

public class EventManager extends Feature
{
    private final Timer logoutTimer;
    private final Timer nameTimer;
    String windowName;
    String windowTitle;
    int titleCharCount;
    boolean advancingTitle;
    
    public EventManager() {
        this.logoutTimer = new Timer();
        this.nameTimer = new Timer();
        this.windowName = "moonlight 1.4.0-beta";
        this.windowTitle = "";
        this.titleCharCount = 1;
        this.advancingTitle = true;
    }
    
    public void init() {
        EventManager.EVENT_BUS.register(this);
    }
    
    public void onUnload() {
        EventManager.EVENT_BUS.unregister(this);
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        EventManager.mc.getWindow().setTitle(this.windowTitle);
        if (this.nameTimer.passedS(0.25)) {
            if (this.titleCharCount >= this.windowName.length() && this.advancingTitle) {
                this.advancingTitle = false;
            }
            else if (this.titleCharCount <= 0 && !this.advancingTitle) {
                this.advancingTitle = true;
            }
            if (this.advancingTitle) {
                ++this.titleCharCount;
                if (this.titleCharCount >= this.windowName.length()) {
                    this.advancingTitle = false;
                }
            }
            else {
                --this.titleCharCount;
                if (this.titleCharCount <= 0) {
                    this.advancingTitle = true;
                }
            }
            this.windowTitle = this.windowName.substring(0, this.titleCharCount);
            this.nameTimer.reset();
        }
        if (!Feature.fullNullCheck()) {
            Moonlight.moduleManager.onUpdate();
            Moonlight.moduleManager.sortModules(true);
            this.onTick();
        }
    }
    
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        Moonlight.moduleManager.onTick();
    }
    
    @Subscribe
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (fullNullCheck()) {
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
    public void onPacketReceive(final PacketEvent.Receive event) {
        Moonlight.serverManager.onPacketReceived();
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            Moonlight.serverManager.update();
        }
    }
    
    @Subscribe
    public void onWorldRender(final Render3DEvent event) {
        Moonlight.moduleManager.onRender3D(event);
    }
    
    @Subscribe
    public void onAttackBlock(final AttackBlockEvent event) {
        Moonlight.moduleManager.AttackBlockEvent(event);
    }
    
    @Subscribe
    public void onPacketSend(final PacketEvent.Send event) {
        Moonlight.moduleManager.Send(event);
    }
    
    @Subscribe
    public void onRenderGameOverlayEvent(final Render2DEvent event) {
        Moonlight.moduleManager.onRender2D(event);
    }
    
    @Subscribe
    public void onKeyInput(final KeyEvent event) {
        Moonlight.moduleManager.onKeyPressed(event.getKey());
    }
    
    @Subscribe
    public void onChatSent(final ChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.cancel();
            try {
                if (event.getMessage().length() > 1) {
                    Moonlight.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                }
                else {
                    final String message = "Please enter a command.";
                    final Text text = (Text)Text.literal(message);
                    EventManager.mc.inGameHud.getChatHud().addMessage(text);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                final String message2 = "An error occurred while running this command. Check the log!";
                final Text text2 = (Text)Text.literal(String.valueOf(Formatting.RED) + message2);
                EventManager.mc.inGameHud.getChatHud().addMessage(text2);
            }
        }
    }
}

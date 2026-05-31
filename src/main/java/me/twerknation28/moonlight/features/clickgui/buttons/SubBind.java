package me.twerknation28.moonlight.features.clickgui.buttons;

import me.twerknation28.moonlight.util.FontUtil;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import me.twerknation28.moonlight.features.settings.Bind;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.features.clickgui.Window;
import me.twerknation28.moonlight.features.clickgui.BaseButton;

public class SubBind extends BaseButton
{
    private final Button parent;
    private final Window window;
    private boolean accepting;
    
    public SubBind(final Button parent) {
        super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 7, 14);
        this.accepting = false;
        parent.getSubEntries().add(this);
        this.parent = parent;
        this.window = parent.getWindow();
    }
    
    @Override
    public void processMouseClick(final int mouseX, final int mouseY, final int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (!this.isMouseHovered()) {
            return;
        }
        if (button == 0) {
            this.accepting = true;
            Util.mc.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void processKeyPress(final char character, final int key) {
        if (!this.accepting) {
            return;
        }
        Bind bind = new Bind(character);
        if (character == '\u0105' || character == '\u0103' || character == '\u0100') {
            bind = new Bind(-1);
        }
        this.parent.getModule().bind.setValue(bind);
        this.accepting = false;
    }
    
    @Override
    public void draw(final DrawContext context, final int mouseX, final int mouseY) {
        String str = this.parent.getModule().getBind().toString().toUpperCase();
        str = str.replace("KEY.KEYBOARD", "").replace(".", " ");
        final String keyName = this.accepting ? "Press a key..." : ("bind: " + str);
        this.y = this.window.getRenderYButton();
        this.x = this.window.getX() + 4;
        this.updateIsMouseHovered(mouseX, mouseY);
        RenderUtil.rect(context.getMatrices(), (float)this.getX(), (float)this.getY(), (float)(this.getWidth() + this.getX()), (float)(this.getHeight() + this.getY()), this.getColor());
        RenderUtil.rect(context.getMatrices(), (float)this.getX(), (float)this.getY(), (float)(-1 + this.getX()), (float)(this.getHeight() + this.getY()), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255));
        FontUtil.drawStringWithShadow(keyName.toLowerCase(), this.getX() + 2, this.getY() + 3, -1, context);
    }
    
    @Override
    public int getColor() {
        if (!this.isMouseHovered()) {
            return ColorUtil.toARGB(0, 0, 0, 50);
        }
        return ColorUtil.toARGB(150, 150, 150, 50);
    }
    
    @Override
    public boolean shouldRender() {
        return this.parent.isOpen() && this.parent.shouldRender();
    }
    
    public Button getParent() {
        return this.parent;
    }
}

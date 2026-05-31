package me.twerknation28.moonlight.features.clickgui;

import net.minecraft.client.gui.DrawContext;

public class BaseButton
{
    protected Window window;
    protected final int width;
    protected int height;
    protected int x;
    protected int y;
    private boolean isHoveredCached;
    
    public BaseButton(final int x, final int y, final int width, final int height) {
        this.isHoveredCached = false;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    protected boolean isMouseHovered() {
        return this.isHoveredCached;
    }
    
    protected void updateIsMouseHovered(final int mouseX, final int mouseY) {
        final int x = this.getX();
        final int y = this.getY();
        final int maxX = x + this.width;
        final int maxY = y + this.height;
        this.isHoveredCached = (x <= mouseX && mouseX <= maxX && y <= mouseY && mouseY <= maxY);
    }
    
    public void processMouseClick(final int mouseX, final int mouseY, final int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
    }
    
    public void processMouseRelease(final int mouseX, final int mouseY, final int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
    }
    
    public void processKeyPress(final char character, final int key) {
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public boolean shouldRender() {
        return this.window.isOpen;
    }
    
    public void openGui() {
    }
    
    public Window getWindow() {
        return this.window;
    }
    
    public boolean isOpen() {
        return false;
    }
    
    public void draw(final DrawContext context, final int p0, final int p1) {
    }
    
    public int getColor() {
        return 0;
    }
}

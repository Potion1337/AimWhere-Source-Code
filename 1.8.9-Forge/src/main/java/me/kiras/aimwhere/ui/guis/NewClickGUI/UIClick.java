package me.kiras.aimwhere.ui.guis.NewClickGUI;
import net.minecraft.client.gui.*;
import java.io.*;

public class UIClick extends GuiScreen
{
    public ClickMenu menu;
    public boolean initialized;
    @Override
    public void initGui() {
        if (!this.initialized) {
            this.menu = new ClickMenu();
            this.initialized = true;
        }
    }
    
    public void load() {
        if (!this.initialized) {
            new Thread(new UIThread(this)).start();
        }
    }
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.menu.draw(mouseX, mouseY);
    }
    
    protected void mouseClicked(final int n, final int n2, final int n3) throws IOException {
        this.menu.mouseClick(n, n2);
    }

    
    protected void mouseReleased(final int n, final int n2, final int n3) {
        this.menu.mouseRelease(n, n2);
    }
    
    protected void actionPerformed(final GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
    }
    
    public void onGuiClosed() {
        super.onGuiClosed();
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
}

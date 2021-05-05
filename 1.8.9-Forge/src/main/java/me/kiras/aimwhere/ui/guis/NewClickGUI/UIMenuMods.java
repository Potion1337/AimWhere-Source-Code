package me.kiras.aimwhere.ui.guis.NewClickGUI;

import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.ui.guis.NewClickGUI.handler.MouseInputHandler;
import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.utils.render.Colors;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;

public class UIMenuMods
{
    private ArrayList<Module> modList;
    private MouseInputHandler handler;
    private MouseInputHandler rightCrink;
    public boolean open;
    public int x;
    public int y;
    public int width;
    public int tab_height;
    private ModuleCategory c;
    public double yPos;
    private boolean opened;
    private boolean closed;
    private int valueYAdd;
    private float scrollY;
    private float scrollAmount;
    
    public UIMenuMods(ModuleCategory c, MouseInputHandler handler) {
        super();
        this.modList = new ArrayList<>();
        this.rightCrink = new MouseInputHandler(1);
        this.valueYAdd = 0;
        this.c = c;
        this.handler = handler;
        this.addModules();
        this.yPos = -(this.y + this.tab_height + this.modList.size() * 20 + 10);
    }
    
    public void draw(int mouseX, int mouseY) {
        this.opened = true;
        int n = 160;
        if (mouseY > this.y + n) {
            mouseY = Integer.MAX_VALUE;
        }
        UnicodeFontRenderer tahoma16 = FontManager.tahoma16;
//        this.c.name().substring(0, 1) + this.c.name().toLowerCase().substring(1, this.c.name().length());
        this.yPos = this.y + this.tab_height - 2;
        int n2 = (int)this.yPos;
        Gui.drawRect(this.x, n2, (this.x + this.width), (n2 + n - 23), -263429);
        int n3 = 15;
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtils.doGlScissor(this.x, this.y + this.tab_height - 2, this.width, scaledResolution.getScaledHeight());
        RenderUtils.doGlScissor(this.x, this.y + this.tab_height - 2, this.width, Math.min(n - (this.tab_height - 2), this.modList.size() * n3 + this.valueYAdd));
        GL11.glTranslated(0.0, this.scrollY, 0.0);
        mouseY -= (int)this.scrollY;
        this.valueYAdd = 0;
        for (Module currentModule : this.modList) {
            if (currentModule.getState()) {
                Gui.drawRect(this.x, n2, (this.x + this.width), (n2 + n3), -14704385);
            }
            boolean hovered = this.yPos == this.y + this.tab_height - 2 && mouseX >= this.x && mouseX <= this.x + this.width - 12 && mouseY >= n2 && mouseY < n2 + n3 && mouseY + this.scrollY >= this.y + this.tab_height;
            if (!LiquidBounce.crink.menu.settingMode) {
                float hoverOpacity;
                if (hovered) {
                    hoverOpacity = (float) RenderUtils.getAnimationState(currentModule.getHoverOpacity(), 0.25f, 1.0f);
                }
                else {
                    hoverOpacity = (float) RenderUtils.getAnimationState(currentModule.getHoverOpacity(), 0.0f, 1.5f);
                }
                currentModule.setHoverOpacity(hoverOpacity);
            }
            else {
                currentModule.setHoverOpacity(0);
            }
            if (hovered && !LiquidBounce.crink.menu.settingMode && this.handler.canExcecute()) {
                currentModule.setState(!currentModule.getState());
            }
            if (hovered && this.rightCrink.canExcecute() && !LiquidBounce.crink.menu.settingMode &&LiquidBounce.crink.menu.currentMod == null && !currentModule.getValues().isEmpty()) {
               LiquidBounce.crink.menu.settingMode = true;
               LiquidBounce.crink.menu.currentMod = currentModule;
            }
            RenderUtils.drawRect((float)this.x, (float)n2, (float)(this.x + this.width), (float)(n2 + n3), ClientUtils.reAlpha(Colors.BLACK.c, currentModule.getHoverOpacity()));
            if (currentModule.getState()) {
                tahoma16.drawString(currentModule.getBreakName(true), this.x + 12.0f, (float)(n2 + (n3 - tahoma16.FONT_HEIGHT) / 2) + 5F, Colors.WHITE.c);
            }
            else {
                tahoma16.drawString(currentModule.getBreakName(true), this.x + 8.0f, (float)(n2 + (n3 - tahoma16.FONT_HEIGHT) / 2) + 5F, Colors.BLACK.c);
            }
            n2 += n3;
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
        if (!LiquidBounce.crink.menu.settingMode ||LiquidBounce.crink.menu.currentMod == null) {
            if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY + this.scrollY >= this.y && mouseY + this.scrollY <= n2) {
                this.scrollY += Mouse.getDWheel() / 10.0f;
            }
            if (n2 - n3 - this.tab_height >= n && n2 - this.y + this.scrollY < (double)n) {
                this.scrollY = n - (float)n2 + this.y;
            }
            if (this.scrollY > 0.0f || n2 - n3 - this.tab_height < n) {
                this.scrollY = 0.0f;
            }
        }
    }
    
    public void mouseClick(int mouseX, int mouseY) {
        mouseX -= (int)this.scrollY;
    }

    public void mouseRelease(int mouseX, int mouseY) {
    }
    
    private void addModules() {
        for (Module mod : LiquidBounce.moduleManager.getModules()) {
            if (mod.getCategory() == this.c)
                this.modList.add(mod);
        }
    }
}

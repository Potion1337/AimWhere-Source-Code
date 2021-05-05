package me.kiras.aimwhere.ui.guis.NewClickGUI;
import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.ui.guis.NewClickGUI.handler.MouseInputHandler;
import me.kiras.aimwhere.utils.fonts.FontManager;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import org.lwjgl.input.Mouse;
import java.awt.Color;
public class ClickMenuCategory
{
    public ModuleCategory c;
    UIMenuMods uiMenuMods;
    private final MouseInputHandler handler;
    public boolean open;
    public int x;
    public int y;
    public int width;
    public int tab_height;
    public int x2;
    public int y2;
    public boolean drag;
    
    public ClickMenuCategory(ModuleCategory c, int x, int y, int width, int tab_height, MouseInputHandler handler) {
        super();
        this.drag = true;
        this.c = c;
        this.x = x;
        this.y = y;
        this.width = width;
        this.tab_height = tab_height;
        this.uiMenuMods = new UIMenuMods(c, handler);
        this.handler = handler;
    }
    
    public void draw(int mouseX, int mouseY) {
        this.open = true;
        this.uiMenuMods.open = true;
        UnicodeFontRenderer tahoma18 = FontManager.tahoma18;
        String string = this.c.getDisplayName();
        RenderUtils.drawRect((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.tab_height), ClientUtils.reAlpha(-658186, 0.95f));
        tahoma18.drawString(string, this.x + 8.0f, (float)(this.y + (this.tab_height - tahoma18.FONT_HEIGHT) / 2), new Color(99, 99, 99).getRGB());
        this.updateUIMenuMods();
        this.uiMenuMods.draw(mouseX, mouseY);
//        if (this.uiMenuMods.open) {
////            try {
////                Method method = Gui.class.getDeclaredMethod("drawGradientRect", int.class, int.class, int.class, int.class, int.class, int.class);
////                method.setAccessible(true);
////                try {
////                    method.invoke(Gui.class,this.x, this.y + this.tab_height - 2, this.x + this.width, this.y + this.tab_height + 3, ClientUtils.reAlpha(Colors.BLACK.c, 0.3f), 0);
////                } catch (IllegalAccessException | InvocationTargetException e) {
////                    e.printStackTrace();
////                }
////            } catch (NoSuchMethodException e) {
////                e.printStackTrace();
////            }
////            drawGradientRect(this.x, this.y + this.tab_height - 2, this.x + this.width, this.y + this.tab_height + 3, ClientUtils.reAlpha(Colors.BLACK.c, 0.3f), 0);
//        }
        if (LiquidBounce.crink.menu.settingMode && LiquidBounce.crink.menu.currentMod != null) {
        }
        else {
            this.move(mouseX, mouseY);
        }
    }


    
    private void move(int n, int n2) {
        if (this.isHovering(n, n2) && this.handler.canExcecute()) {
            this.drag = true;
            this.x2 = n - this.x;
            this.y2 = n2 - this.y;
        }
        if (!Mouse.isButtonDown(0)) {
            this.drag = false;
        }
        if (this.drag) {
            this.x = n - this.x2;
            this.y = n2 - this.y2;
        }
    }
    
    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.tab_height;
    }
    
    private void updateUIMenuMods() {
        this.uiMenuMods.x = this.x;
        this.uiMenuMods.y = this.y;
        this.uiMenuMods.tab_height = this.tab_height;
        this.uiMenuMods.width = this.width;
    }
    
    public void mouseClick(int mouseX, int mouseY) {
        this.uiMenuMods.mouseClick(mouseX, mouseY);
    }

    public void mouseRelease(int mouseX, int mouseY) {
        this.uiMenuMods.mouseRelease(mouseX, mouseY);
    }
}

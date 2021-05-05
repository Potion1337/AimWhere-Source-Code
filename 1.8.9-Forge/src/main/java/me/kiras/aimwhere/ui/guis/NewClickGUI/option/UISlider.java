package me.kiras.aimwhere.ui.guis.NewClickGUI.option;

import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.utils.render.Colors;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.Value;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.awt.Color;

public class UISlider
{
    Value<?> myValue;
    public float x;
    public float y;
    private boolean isDraging;
    private boolean clickNotDraging;
    public boolean showValue;
    public int mouseX;
    public int mouseY;
    public int dragX;
    public int dragY;
    float ani;
    
    public UISlider(final IntegerValue value) {
        super();
        this.myValue = value;
    }

    public UISlider(final FloatValue value) {
        super();
        this.myValue = value;
    }
    
    public void draw(final float x, final float y) {
        this.x = x;
        this.y = y;
        final UnicodeFontRenderer tahoma20 = FontManager.tahoma20;
        if (myValue instanceof IntegerValue) {
            IntegerValue value = (IntegerValue) myValue;
            final double n = (value.get() - value.getMinimum()) / (value.getMaximum() - value.getMinimum());
            tahoma20.drawString(value.getName(), x - 198.0f, y - 2.0f, Colors.BLACK.c);
            if (this.showValue) {
                FontManager.tahoma15.drawString(value.get() + "", x - 78.0f - tahoma20.getStringWidth(value.get() + ""), y, Colors.GREY.c);
            }
            if (this.ani == 0.0f) {
                this.ani = (float) (x - 15.0f - (60.0 - 60.0 * n));
            }
            this.ani = (float) RenderUtils.getAnimationState(this.ani, (float) (x - 15.0f - (60.0 - 60.0 * n)), (float) Math.max(10.0, Math.abs(this.ani - (x - 15.0f - (60.0 - 60.0 * n))) * 30.0 * 0.3));
            RenderUtils.drawRoundedRect(x - 75.0f, y + 3.0f, x - 15.0f, y + 6.0f, (int) 1.0f, new Color(Colors.GREY.c).brighter().brighter().getRGB());
            RenderUtils.drawRoundedRect(x - 75.0f, y + 3.0f, this.ani, y + 6.0f, (int) 1.0f, new Color(-14848033).brighter().getRGB());
            RenderUtils.circle(this.ani, y + 4.5f, 4.0f, new Color(-14848033).brighter().getRGB());
        }
        if (myValue instanceof FloatValue) {
            FloatValue value = (FloatValue) myValue;
            final float n = (value.get() - value.getMinimum()) / (value.getMaximum() - value.getMinimum());
            tahoma20.drawString(value.getName(), x - 198.0f, y - 2.0f, Colors.BLACK.c);
            if (this.showValue) {
                FontManager.tahoma15.drawString(value.get() + "", x - 78.0f - tahoma20.getStringWidth(value.get() + ""), y, Colors.GREY.c);
            }
            if (this.ani == 0.0f) {
                this.ani = (float) (x - 15.0f - (60.0 - 60.0 * n));
            }
            this.ani = (float) RenderUtils.getAnimationState(this.ani, (float) (x - 15.0f - (60.0 - 60.0 * n)), (float) Math.max(10.0, Math.abs(this.ani - (x - 15.0f - (60.0 - 60.0 * n))) * 30.0 * 0.3));
            RenderUtils.drawRoundedRect(x - 75.0f, y + 3.0f, x - 15.0f, y + 6.0f, (int) 1.0f, new Color(Colors.GREY.c).brighter().brighter().getRGB());
            RenderUtils.drawRoundedRect(x - 75.0f, y + 3.0f, this.ani, y + 6.0f, (int) 1.0f, new Color(-14848033).brighter().getRGB());
            RenderUtils.circle(this.ani, y + 4.5f, 4.0f, new Color(-14848033).brighter().getRGB());
        }
    }
    
    public void onPress(final int mouseX, final int mouseY) {
        this.showValue = this.isHovering(mouseX, mouseY, this.x - 100.0f, this.y - 3.0f, this.x - 10.0f, this.y + 10.0f);
        if (Mouse.isButtonDown(0)) {
            if (this.isHovering(mouseX, mouseY, this.x - 75.0f, this.y - 3.0f, this.x - 15.0f, this.y + 10.0f) || this.isDraging) {
                this.isDraging = true;
            }
            else {
                this.clickNotDraging = true;
            }
            if (this.isDraging && !this.clickNotDraging) {
                double n = (mouseX - this.x + 75.0f) / 60.0f;
                if (n < 0.0) {
                    n = 0.0;
                }
                if (n > 1.0) {
                    n = 1.0;
                }
                if(myValue instanceof IntegerValue) {
                    IntegerValue value = (IntegerValue) myValue;
                    final int n2 = (int) (Math.round(((value.getMaximum() - value.getMinimum()) * n + value.getMinimum()) * (1.0 / value.getSteps())) / (1.0 / value.getSteps()));
                    value.set(n2);
                }
                if(myValue instanceof FloatValue) {
                    FloatValue value = (FloatValue) myValue;
                    final float n2 = (float) (Math.round(((value.getMaximum() - value.getMinimum()) * n + value.getMinimum()) * (1.0 / value.getSteps())) / (1.0 / value.getSteps()));
                    value.set(n2);
                }
            }
        }
        else {
            this.isDraging = false;
            this.clickNotDraging = false;
        }
        if (this.isDraging && !LiquidBounce.crink.menu.isDraggingSlider) {
            LiquidBounce.crink.menu.isDraggingSlider = true;
        }
        else {
            LiquidBounce.crink.menu.isDraggingSlider = false;
        }
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    
    private boolean isHovering(final int n, final int n2, final double n3, final double n4, final double n5, final double n6) {
        return n > n3 && n < n5 && n2 > n4 && n2 < n6;
    }
}

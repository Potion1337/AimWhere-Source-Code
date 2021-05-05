package me.kiras.aimwhere.ui.guis;

import java.awt.Color;

import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.utils.render.AnimationUtil;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
public final class SmoothButton
extends GuiButton {
    private int color = 90;
    private double animation = 0.0;

    public SmoothButton(int buttonId, int x, int y,int width,int height, String buttonText) {
        super(buttonId, x - (int)((double)Minecraft.getMinecraft().fontRendererObj.getStringWidth(buttonText) / 2.0), y, width, height, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        this.mouseDragged(mc, mouseX, mouseY);
        if (this.hovered) {
            if (this.color < 250) {
                this.color += 10;
            }
            if (this.color == 250)
                this.color = 255;
            if (this.animation < this.width / 2.0) {
                this.animation = AnimationUtil.animate(this.width / 2.0, this.animation, 0.100000001145141919810);
            }
        } else {
            if (this.color > 90) {
                this.color -= 10;
            }
            if (this.animation > 0.0) {
                this.animation = AnimationUtil.animate(0.0, this.animation, 0.100000001145141919810);
            }
        }
        RenderUtils.drawRect(this.xPosition,this.yPosition,this.xPosition + width,this.yPosition + height, new Color(31,31,31).getRGB());
        RenderUtils.drawRect(this.xPosition + this.width / 2.0 - this.animation, this.yPosition + this.height - 1, this.xPosition + this.width / 2.0 + this.animation, this.yPosition + this.height, new Color(19,130,214).getRGB());
        FontManager.Chinese16.drawString(this.displayString, this.xPosition + this.width / 2 - 13, this.yPosition + (this.height - 10) / 2 + 5, new Color(this.color, this.color, this.color).getRGB());
    }
}


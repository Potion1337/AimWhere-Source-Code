package me.kiras.aimwhere.ui.guis.screens.clickgui;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.ColorValue;

import java.awt.Color;

public class ColorSettings {

    static int x;
    static int y = 0;
    static int ticks;

    public void render(ColorValue colorValue, int x, int y, int mouseX, int mouseY, boolean isKeyDown) {
        this.x=x;
        this.y=y;
        for(ticks = 0;ticks<50;ticks+=1) {
            Color rainbowcolor = new Color(Color.HSBtoRGB((float) (ticks / 50.0 + Math.sin(1.6)) % 1.0f, 0.5f, 1.0f));
            if(mouseX>x&&mouseX<x + 50&&mouseY>y&&mouseY<y+13 && isKeyDown){
//                color = new Color(Color.HSBtoRGB((float) ((mouseX - x) / 50.0 + Math.sin(1.6)) % 1.0f, 0.5f, 1.0f)).getRGB();
                colorValue.set(new Color(Color.HSBtoRGB((float) ((mouseX - x) / 50.0 + Math.sin(1.6)) % 1.0f, 0.5f, 1.0f)).getRGB());
                RenderUtils.drawRect(mouseX-1,mouseY-1,mouseX+1,mouseY+1,new Color(100,100,100,100).getRGB());
            }

            if(mouseX>x&&mouseX<x + 50&&mouseY>y&&mouseY<y+13){
                RenderUtils.drawRect(mouseX-1,mouseY-1,mouseX+1,mouseY+1,new Color(100,100,100,100).getRGB());
            }

            RenderUtils.drawRect(x + ticks, y, x + ticks + 1, y + 13, rainbowcolor.getRGB());
            RenderUtils.drawRect(x , y+16, x + 50, y + 20,colorValue.get());
        }
        if (++ticks > 50) {
            ticks = 0;
        }
    }
}


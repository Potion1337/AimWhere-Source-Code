package me.kiras.aimwhere.utils.render;
import java.awt.Color;
import java.util.List;

import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.utils.fonts.FontManager;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class CompassUtil extends MinecraftInstance {
    UnicodeFontRenderer yahei28U = FontManager.array20;
    UnicodeFontRenderer yahei18U = FontManager.array14;
    UnicodeFontRenderer yahei22U = FontManager.array16;
    public List<Degree> degrees = Lists.newArrayList();
    public CompassUtil() {
        degrees.add(new Degree("N", 1));
        degrees.add(new Degree("195", 2));
        degrees.add(new Degree("210", 2));
        degrees.add(new Degree("NE", 3));
        degrees.add(new Degree("240", 2));
        degrees.add(new Degree("255", 2));
        degrees.add(new Degree("E", 1));
        degrees.add(new Degree("285", 2));
        degrees.add(new Degree("300", 2));
        degrees.add(new Degree("SE", 3));
        degrees.add(new Degree("330", 2));
        degrees.add(new Degree("345", 2));
        degrees.add(new Degree("S", 1));
        degrees.add(new Degree("15", 2));
        degrees.add(new Degree("30", 2));
        degrees.add(new Degree("SW", 3));
        degrees.add(new Degree("60", 2));
        degrees.add(new Degree("75", 2));
        degrees.add(new Degree("W", 1));
        degrees.add(new Degree("105", 2));
        degrees.add(new Degree("120", 2));
        degrees.add(new Degree("NW", 3));
        degrees.add(new Degree("150", 2));
        degrees.add(new Degree("165", 2));
    }

    public void draw() {
        ScaledResolution sr = new ScaledResolution(mc);
        preRender();
        GlStateManager.enableBlend();
        float center = sr.getScaledWidth() / 2;
        int count = 0;
        float yaaahhrewindTime = (mc.thePlayer.rotationYaw % 360) * 2 + 360 * 3;
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtils.doGlScissor(sr.getScaledWidth() / 2 - 120, 25, 220, 25);
        for (Degree d : degrees) {
            float location = center + (count * 30) - yaaahhrewindTime;
            float completeLocation = (d.type == 1 ? (location - this.yahei28U.getStringWidth(d.text) / 2)
                    : d.type == 2 ? (location - this.yahei18U.getStringWidth(d.text) / 2)
                    : (location - this.yahei22U.getStringWidth(d.text) / 2));

            int opacity = opacity(sr, completeLocation);

            if (d.type == 1 && opacity != 16777215) {
                GlStateManager.color(1, 1, 1, 1);
                this.yahei28U.drawString(d.text, completeLocation, -75 + 100, opacity(sr, completeLocation));
            }

            if (d.type == 2 && opacity != 16777215) {
                GlStateManager.color(1, 1, 1, 1);
                RenderUtils.drawRect(location - 0.5f, -75 + 100 + 4, location + 0.5f, -75 + 105 + 4,
                        opacity(sr, completeLocation));
                GlStateManager.color(1, 1, 1, 1);
                this.yahei18U.drawString(d.text, completeLocation, -75 + 105 + 3.5f + 4, opacity(sr, completeLocation));
            }

            if (d.type == 3 && opacity != 16777215) {
                GlStateManager.color(1, 1, 1, 1);
                this.yahei22U.drawString(d.text, completeLocation,
                        -75 + 100 + this.yahei28U.FONT_HEIGHT / 2 - this.yahei22U.FONT_HEIGHT / 2,
                        opacity(sr, completeLocation));
            }

            count++;
        }
        for (Degree d : degrees) {

            float location = center + (count * 30) - yaaahhrewindTime;
            float completeLocation = (float) (d.type == 1 ? (location - this.yahei28U.getStringWidth(d.text) / 2)
                    : d.type == 2 ? (location - this.yahei18U.getStringWidth(d.text) / 2)
                    : (location - this.yahei22U.getStringWidth(d.text) / 2));

            if (d.type == 1) {
                GlStateManager.color(1, 1, 1, 1);
                this.yahei28U.drawString(d.text, completeLocation, -75 + 100, opacity(sr, completeLocation));
            }

            if (d.type == 2) {
                GlStateManager.color(1, 1, 1, 1);
                RenderUtils.drawRect(location - 0.5f, -75 + 100 + 4, location + 0.5f, -75 + 105 + 4,
                        opacity(sr, completeLocation));
                GlStateManager.color(1, 1, 1, 1);
                this.yahei18U.drawString(d.text, completeLocation, -75 + 105 + 3.5f + 4, opacity(sr, completeLocation));
            }

            if (d.type == 3) {
                GlStateManager.color(1, 1, 1, 1);
                this.yahei22U.drawString(d.text, completeLocation,
                        -75 + 100 + this.yahei28U.FONT_HEIGHT / 2 - this.yahei22U.FONT_HEIGHT / 2,
                        opacity(sr, completeLocation));
            }

            count++;
        }
        for (Degree d : degrees) {
            float location = center + (count * 30) - yaaahhrewindTime;
            float completeLocation = (d.type == 1 ? (location - this.yahei28U.getStringWidth(d.text) / 2)
                    : d.type == 2 ? (location - this.yahei18U.getStringWidth(d.text) / 2)
                    : (location - this.yahei22U.getStringWidth(d.text) / 2));

            if (d.type == 1) {
                GlStateManager.color(1, 1, 1, 1);
                this.yahei28U.drawString(d.text, completeLocation, -75 + 100, opacity(sr, completeLocation));
            }

            if (d.type == 2) {
                GlStateManager.color(1, 1, 1, 1);
                RenderUtils.drawRect(location - 0.5f, -75 + 100 + 4, location + 0.5f, -75 + 105 + 4,
                        opacity(sr, completeLocation));
                GlStateManager.color(1, 1, 1, 1);
                this.yahei18U.drawString(d.text, completeLocation, -75 + 105 + 3.5f + 4, opacity(sr, completeLocation));
            }

            if (d.type == 3) {
                GlStateManager.color(1, 1, 1, 1);
                this.yahei22U.drawString(d.text, completeLocation,
                        -75 + 100 + this.yahei28U.FONT_HEIGHT / 2 - this.yahei22U.FONT_HEIGHT / 2,
                        opacity(sr, completeLocation));
            }

            count++;
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
        RenderUtils.drawRect(0, 0, 0, 0, -1);
    }
    public void preRender() {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
    }
    public int opacity(ScaledResolution sr, float offset) {
        return new Color(255, 255, 255, (int) Math.min(Math.max(0, 255 - Math.abs(sr.getScaledWidth() / 2 - offset) * 1.8F), 255)).getRGB();
    }
}

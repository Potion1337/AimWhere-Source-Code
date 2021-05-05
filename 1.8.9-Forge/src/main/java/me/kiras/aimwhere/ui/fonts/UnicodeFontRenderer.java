package me.kiras.aimwhere.ui.fonts;
import java.awt.Font;

import me.kiras.aimwhere.libraries.slick.Color;
import me.kiras.aimwhere.libraries.slick.SlickException;
import me.kiras.aimwhere.libraries.slick.UnicodeFont;
import me.kiras.aimwhere.libraries.slick.font.effects.ColorEffect;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.TextEvent;
import net.ccbluex.liquidbounce.features.module.modules.misc.NameProtect;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("ALL")
public class UnicodeFontRenderer
        extends FontRenderer {
    private final UnicodeFont font;
    public UnicodeFontRenderer(Font awtFont, boolean unicode) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
        this.font = new UnicodeFont(awtFont.deriveFont(Font.PLAIN));
        this.font.addAsciiGlyphs();
        font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        if (unicode)
            this.font.addGlyphs(0, 65535);
        try {
            this.font.loadGlyphs();
        }
        catch (SlickException slickException) {
            throw new RuntimeException(slickException);
        }
        this.FONT_HEIGHT = this.font.getHeight("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789") / 2;
    }

    public String replaceColor(String str) {
        str = str.replaceAll("§1", "§0");
        str = str.replaceAll("§2", "§0");
        str = str.replaceAll("§3", "§0");
        str = str.replaceAll("§4", "§0");
        str = str.replaceAll("§5", "§0");
        str = str.replaceAll("§6", "§0");
        str = str.replaceAll("§7", "§0");
        str = str.replaceAll("§8", "§0");
        str = str.replaceAll("§9", "§0");
        str = str.replaceAll("§0", "§0");
        str = str.replaceAll("§a", "§0");
        str = str.replaceAll("§b", "§0");
        str = str.replaceAll("§c", "§0");
        str = str.replaceAll("§d", "§0");
        str = str.replaceAll("§e", "§0");
        str = str.replaceAll("§f", "§0");
        str = str.replaceAll("§r", "§0");
        str = str.replaceAll("§A", "§0");
        str = str.replaceAll("§B", "§0");
        str = str.replaceAll("§C", "§0");
        str = str.replaceAll("§D", "§0");
        str = str.replaceAll("§E", "§0");
        str = str.replaceAll("§F", "§0");
        str = str.replaceAll("§R", "§0");
        str = str.replaceAll("§r", "§0");
        return str;
    }

    public int drawString(String text, float x, float y, boolean always) {
        String[] array;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            java.awt.Color col = java.awt.Color.BLACK;
            str = str.substring(1, str.length());
            this.Draw(str, x + len, y, new java.awt.Color(col.getRed(), col.getGreen(), col.getBlue()).getRGB());
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawString(String text, int x, int y, int color) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            this.Draw(str, (float)x + len, y, new java.awt.Color(col.getRed(), col.getGreen(), col.getBlue()).getRGB());
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawString(String text, float x, float y, int color, boolean dorpshadow) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            if (dorpshadow) {
                this.Draw(str, x + len + 0.5f, y + 0.5f, this.getColor(0, 0, 0, 80));
            }
            this.Draw(str, x + len, y, new java.awt.Color(col.getRed(), col.getGreen(), col.getBlue()).getRGB());
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawStringForChat(String text, float x, float y, int color, boolean dorpshadow) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;

        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            if (dorpshadow) {
                this.Draw(str, x + len + 0.5F, y + 1, this.getColor(0, 0, 0, 150));
            }
            this.Draw(str, x + len, y, new java.awt.Color(col.getRed(), col.getGreen(), col.getBlue()).getRGB());
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawString(String text, float x, float y, int color, int alpha) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            this.Draw(str, x + len, y, new java.awt.Color(col.getRed(), col.getGreen(), alpha).getRGB());
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawStringWithShadow(String text, float x, float y, int color, int alpha) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            int Shadowcolor = (color & 16579836) >> 2 | color & -16777216;
            this.Draw(str, x + len + 0.5f, y + 0.5f, this.getColor(0, 0, 0, 150));
            this.Draw(str, x + len, y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), alpha));
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawString(String text, float x, float y, int color) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            this.Draw(str, x + len, y, new java.awt.Color(col.getRed(), col.getGreen(), col.getBlue()).getRGB());
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        int color1 = color | alpha << 24;
        color1 |= red << 16;
        color1 |= green << 8;
        return color1 |= blue;
    }


    /**
     * @param string
     * @param x
     * @param y
     * @param color
     */
    private void Draw(String string, float x, float y, int color) {
        TextEvent event = new TextEvent(string);
        LiquidBounce.eventManager.callEvent(event);
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        boolean blend = GL11.glIsEnabled(3042);
        boolean lighting = GL11.glIsEnabled(2896);
//        boolean texture = GL11.glIsEnabled(3553);
//        if (!blend) {
//            GL11.glEnable(3042);
//        }
//        if (lighting) {
//            GL11.glDisable(2896);
//        }
//        if (texture) {
//            GL11.glDisable(3553);
//        }
        y = y * 2.0f - 8.0f;
        this.font.drawString(x *= 2.0f, y, event.getText(), new Color(color));
//        if (texture) {
//            GL11.glEnable(3553);
//        }
//        if (lighting) {
//            GL11.glEnable(2896);
//        }
//        if (!blend) {
//            GL11.glDisable(3042);
//        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glPopMatrix();
        //GlStateManager.bindCurrentTexture();
    }

    public int drawStringWithShadow(String text, int x, int y, int color) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            int Shadowcolor = (color & 16579836) >> 2 | color & -16777216;
            this.Draw(str, (float)x + len + 0.5f, (float)y + 0.5f, this.getColor(0, 0, 0, 150));
            this.Draw(str, (float)x + len, y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()));
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        String[] array;
        int ColorBak = color;
        String Nameprotect = text;

        if (Nameprotect == null) {
            return 0;
        }
        Nameprotect = "§r" + Nameprotect + "§r";
        float len = -1.0f;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            switch (str.charAt(0)) {
                case '0': {
                    color = new java.awt.Color(0, 0, 0).getRGB();
                    break;
                }
                case '1': {
                    color = new java.awt.Color(0, 0, 170).getRGB();
                    break;
                }
                case '2': {
                    color = new java.awt.Color(0, 170, 0).getRGB();
                    break;
                }
                case '3': {
                    color = new java.awt.Color(0, 170, 170).getRGB();
                    break;
                }
                case '4': {
                    color = new java.awt.Color(170, 0, 0).getRGB();
                    break;
                }
                case '5': {
                    color = new java.awt.Color(170, 0, 170).getRGB();
                    break;
                }
                case '6': {
                    color = new java.awt.Color(255, 170, 0).getRGB();
                    break;
                }
                case '7': {
                    color = new java.awt.Color(170, 170, 170).getRGB();
                    break;
                }
                case '8': {
                    color = new java.awt.Color(85, 85, 85).getRGB();
                    break;
                }
                case '9': {
                    color = new java.awt.Color(85, 85, 255).getRGB();
                    break;
                }
                case 'a': {
                    color = new java.awt.Color(85, 255, 85).getRGB();
                    break;
                }
                case 'b': {
                    color = new java.awt.Color(85, 255, 255).getRGB();
                    break;
                }
                case 'c': {
                    color = new java.awt.Color(255, 85, 85).getRGB();
                    break;
                }
                case 'd': {
                    color = new java.awt.Color(255, 85, 255).getRGB();
                    break;
                }
                case 'e': {
                    color = new java.awt.Color(255, 255, 85).getRGB();
                    break;
                }
                case 'f': {
                    color = new java.awt.Color(255, 255, 255).getRGB();
                    break;
                }
                case 'r': {
                    color = ColorBak;
                }
            }
            java.awt.Color col = new java.awt.Color(color);
            str = str.substring(1, str.length());
            int Shadowcolor = (color & 16579836) >> 2 | color & -16777216;
            this.Draw(str, x + len + 0.5f, y + 0.5f, this.getColor(0, 0, 0, 150));
            this.Draw(str, x + len, y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()));
            len += (float)(this.getStringWidth(str) + 1);
        }
        return (int)len;
    }

    public int getCharWidth(char c) {
        return this.getStringWidth(Character.toString((char)c));
    }

    public int getStringWidth(String string) {
        String[] array;
        String Nameprotect = string;
        float len = -1.0f;
        Nameprotect = "§r" + Nameprotect;
        String[] arrstring = array = Nameprotect.split("§");
        int n = array.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            if (str.length() < 1) continue;
            str = str.substring(1, str.length());
            len += (float)(this.font.getWidth(str) / 2 + 1);
        }
        return (int)len;
    }

    public int getStringHeight(String string) {
        return this.font.getHeight(string) / 2;
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        this.drawString(text, x - (float)(this.getStringWidth(text) / 2), y, color);
    }
}

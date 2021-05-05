package me.kiras.aimwhere.utils.fonts;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import me.kiras.aimwhere.ui.fonts.ClientFont;
import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public final class FontManager {
    private static final HashMap<String, HashMap<Float, UnicodeFontRenderer>> fonts = new HashMap<>();
    public static UnicodeFontRenderer array14 = getFont("Array",14,true,false);
    public static UnicodeFontRenderer array15 = getFont("Array",15,true,false);
    public static UnicodeFontRenderer array16 = getFont("Array",16,true,false);
    public static UnicodeFontRenderer array18 = getFont("Array",18,true,false);
    public static UnicodeFontRenderer array35 = getFont("Array",35,true,false);
    public static UnicodeFontRenderer array20 = getFont("Array",20,true,false);
    public static UnicodeFontRenderer array28 = getFont("Array", 28, true, false);
    public static GameFontRenderer array16Game = getGameFont("Array", 33, true);
    public static GameFontRenderer array18Game = getGameFont("Array", 35, true);
    public static UnicodeFontRenderer Chinese14 = getFont("Chinese", 14, false, true);
    public static UnicodeFontRenderer Chinese16 = getFont("Chinese", 16, false, true);
    public static UnicodeFontRenderer Chinese18 = getFont("Chinese", 18, false, true);
    public static UnicodeFontRenderer Chinese17 = getFont("Chinese", 17, false, true);
    public static UnicodeFontRenderer tahoma15 = getFont("Tahoma", 15, false,false);
    public static UnicodeFontRenderer tahoma16 = getFont("Tahoma", 16, false,false);
    public static UnicodeFontRenderer tahoma18 = getFont("Tahoma", 18, false,false);
    public static UnicodeFontRenderer tahoma20 = getFont("Tahoma", 20, false,false);
    public static UnicodeFontRenderer tahoma25 = getFont("Tahoma", 25, false,false);
    public static UnicodeFontRenderer tahoma30 = getFont("Tahoma", 30, false,false);
    public static UnicodeFontRenderer Icon30 = getFont("Icon", 30, false,false);

    //    public static void initFontManager() {
////        long time = System.currentTimeMillis();
//        array16Game = getGameFont("Array", 33, true);
//        array18Game = getGameFont("Array", 35, true);
//        new Thread(() ->{
//            array15 = getFont("Array",15,true);
//        }).start();
//        new Thread(() ->{
//            array16 = getFont("Array",16,true);
//        }).start();
//        new Thread(() ->{
//            array18 = getFont("Array",18,true);
//        }).start();
//        new Thread(() ->{
//            array35 = getFont("Array",35,true);
//        }).start();
//        new Thread(() ->{
//            array20 = getFont("Array",20,true);
//        }).start();
//        new Thread(() ->{
//            Chinese14 = getFont("Chinese", 14, false);
//        }).start();
//        new Thread(() ->{
//            Chinese16 = getFont("Chinese", 16, false);
//        }).start();
//        new Thread(() ->{
//            Chinese17 = getFont("Chinese", 17, false);
//        }).start();
//        new Thread(() ->{
//            tahoma15 = getFont("Tahoma", 15, false);
//        }).start();
//        new Thread(() ->{
//            tahoma16 = getFont("Tahoma", 16, false);
//        }).start();
//        new Thread(() ->{
//            tahoma18 = getFont("Tahoma", 18, false);
//        }).start();
//        new Thread(() ->{
//            tahoma20 = getFont("Tahoma", 20, false);
//        }).start();
//        new Thread(() ->{
//            tahoma25 = getFont("Tahoma", 25, false);
//        }).start();
//        new Thread(() ->{
//            tahoma30 = getFont("Tahoma", 30, false);
//        }).start();
//        new Thread(() ->{
//            Icon30 = getFont("Icon", 30, false);
//        }).start();
////        ClientUtils.getLogger().debug("[FontManager] Loaded fonts use " + (time - System.currentTimeMillis()) + "ms");
//    }
    private static ClientFont getClientFont(final String fontName, final int size, final boolean otf) {
        try {
            final InputStream inputStream = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("AimWhere/fonts/" + fontName + "."+ (otf ? "otf" : "ttf"))).getInputStream();
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return new ClientFont(awtClientFont);
        }catch(final Exception e) {
            e.printStackTrace();
            return new ClientFont(new Font("default", Font.PLAIN, size));
        }
    }
    private static GameFontRenderer getGameFont(final String fontName, final int size, final boolean otf) {
        try {
            final InputStream inputStream = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("AimWhere/fonts/" + fontName + "."+ (otf ? "otf" : "ttf"))).getInputStream();
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return new GameFontRenderer(awtClientFont);
        }catch(final Exception e) {
            e.printStackTrace();
            return new GameFontRenderer(new Font("default", Font.PLAIN, size));
        }
    }

    private static UnicodeFontRenderer getFont(String fontName, float size, boolean otf,boolean unicode) {
        UnicodeFontRenderer tempFont = null;
        try {
            if (fonts.containsKey(fontName) && (fonts.get(fontName)).containsKey(size)) {
                return fonts.get(fontName).get(size);
            }
            tempFont = new UnicodeFontRenderer(Font.createFont(0,Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("AimWhere/fonts/" + fontName + "."+ (otf ? "otf" : "ttf"))).getInputStream()).deriveFont(size), unicode);
            tempFont.setUnicodeFlag(true);
            tempFont.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
            HashMap<Float, UnicodeFontRenderer> hashMap = new HashMap<>();
            if (fonts.containsKey(fontName)) {
                hashMap.putAll(fonts.get(fontName));
            }
            hashMap.put(size, tempFont);
            fonts.put(fontName, hashMap);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return tempFont;
    }
}

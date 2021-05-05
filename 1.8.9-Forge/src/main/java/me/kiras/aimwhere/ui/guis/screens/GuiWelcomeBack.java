package me.kiras.aimwhere.ui.guis.screens;

import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.utils.fonts.FontManager;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class GuiWelcomeBack extends GuiScreen
{
    private static long startTime = 0L;
    int alpha;
    private float currentX;
    private float currentY;
    int textalpha;
    double Anitext;

    public GuiWelcomeBack() {
        super();
        this.alpha = 255;
        this.textalpha = 255;
        this.Anitext = 0.0;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis();
        }
        final int h = new ScaledResolution(this.mc).getScaledHeight();
        final int w = new ScaledResolution(this.mc).getScaledWidth();
        final ScaledResolution sr = new ScaledResolution(this.mc);
        final float xDiff = (mouseX - h / 2 - this.currentX) / sr.getScaleFactor();
        final float yDiff = (mouseY - w / 2 - this.currentY) / sr.getScaleFactor();
        this.currentX += xDiff * 0.3f;
        this.currentY += yDiff * 0.3f;
        final UnicodeFontRenderer fontwel2 = FontManager.array18;
        final UnicodeFontRenderer fontwel3 = FontManager.array35;
        this.drawBackground(0);
        GlStateManager.translate(this.currentX / 100.0f, this.currentY / 100.0f, 0.0f);
        GlStateManager.translate(this.currentX / 15.0f, this.currentY / 15.0f, 0.0f);
        GlStateManager.translate(-this.currentX / 100.0f, -this.currentY / 100.0f, 0.0f);
        fontwel3.drawStringWithShadow("Welcome back to AimWhere", sr.getScaledWidth() / 2.0f - 75, sr.getScaledHeight() / 2.0f - 3.0f - (float)this.Anitext, new Color(255, 255, 255).getRGB(), 255);
        fontwel2.drawStringWithShadow("Welcome back," + LiquidBounce.INSTANCE.getIlliIllliiI()[1], sr.getScaledWidth() / 2.0f - 75, sr.getScaledHeight() / 2.0f + fontwel3.getStringHeight("BYE") - (float)this.Anitext, new Color(255, 255, 255).getRGB(), 255);
        if (this.alpha != 255) {
            ++this.alpha;
        }
        if (this.alpha != 255) {
            ++this.alpha;
        }
        if (this.alpha != 255) {
            ++this.alpha;
        }
        if (this.alpha != 255) {
            ++this.alpha;
        }
        if (this.alpha >= 255 && startTime + 3000L <= System.currentTimeMillis()) {
            this.mc.displayGuiScreen(new MainMenuScreen());
        }
    }
}

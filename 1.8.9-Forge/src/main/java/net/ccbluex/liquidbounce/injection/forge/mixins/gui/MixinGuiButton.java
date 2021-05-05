/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.utils.fonts.FontManager;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

import static net.minecraft.client.gui.Gui.*;


@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {

   @Shadow
   public boolean visible;

   @Shadow
   public int xPosition;

   @Shadow
   public int yPosition;

   @Shadow
   public int width;

   @Shadow
   public int height;

   @Shadow
   protected boolean hovered;

   @Shadow
   public boolean enabled;

   @Shadow
   protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

   @Shadow
   public String displayString;

   @Shadow
   @Final
   protected static ResourceLocation buttonTextures;
   private float cut;
   private float alpha;

   /**
    * @author CCBlueX
    */
   @Overwrite
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (visible) {
         //final FontRenderer fontRenderer =
         //   mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.font35;
         final FontRenderer fontRenderer;
         if((Object) this instanceof GuiButton && !((Object) this instanceof GuiSlider) && !((Object) this instanceof GuiOptionSlider))
            fontRenderer = FontManager.Chinese16;
         else
            fontRenderer = Fonts.font35;
         hovered = ((mouseX >= this.xPosition) && (mouseY >= this.yPosition) &&
                 (mouseX < (this.xPosition + this.width)) && (mouseY < (this.yPosition + this.height)));

         final int delta;
         delta = RenderUtils.deltaTime;

         if (!enabled || !hovered) {
            cut -= 0.05F * delta;

            if (cut <= 0) cut = 0;

            alpha -= 0.3F * delta;

            if (alpha <= 120) alpha = 120;
         } else {
            cut += 0.05F * delta;

            if (cut >= 4) cut = 4;

            alpha += 0.3F * delta;

            if (alpha >= 210) alpha = 210;
         }

         drawRect(this.xPosition + (int) this.cut, this.yPosition,
                 this.xPosition + this.width - (int) this.cut, this.yPosition + this.height,
                 this.enabled ? new Color(0F, 0F, 0F, this.alpha / 255F).getRGB() :
                         new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());

         mc.getTextureManager().bindTexture(buttonTextures);
         mouseDragged(mc, mouseX, mouseY);

         AWTFontRenderer.Companion.setAssumeNonVolatile(true);

         fontRenderer.drawStringWithShadow(ColorUtils.stripColor(displayString),
                 (float) ((this.xPosition + this.width / 2) -
                         fontRenderer.getStringWidth(displayString) / 2),
                 this.yPosition + (this.height - 5) / 2F + 3, 14737632);

         AWTFontRenderer.Companion.setAssumeNonVolatile(false);

         GlStateManager.resetColor();
      }
   }
}
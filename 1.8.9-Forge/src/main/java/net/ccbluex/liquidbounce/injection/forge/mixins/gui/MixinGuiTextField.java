package net.ccbluex.liquidbounce.injection.forge.mixins.gui;
import me.kiras.aimwhere.utils.fonts.FontManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(GuiTextField.class)
public class MixinGuiTextField {
    @Shadow
    private int lineScrollOffset;
    @Shadow
    private int cursorPosition;
    @Shadow
    @Final
    private FontRenderer fontRendererInstance;
    @Shadow
    public int xPosition;
    @Shadow
    public int yPosition;

    /** The width of this text field. */
    @Shadow
    @Final
    private int width;
    @Shadow
    @Final
    private int height;

    /** Has the current text being edited on the textbox. */
    @Shadow
    private String text = "";
    @Shadow
    private int maxStringLength = 32;
    @Shadow
    private boolean enableBackgroundDrawing = true;

    @Shadow
    private int cursorCounter;

    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    @Shadow
    private boolean isEnabled = true;

    @Shadow
    private boolean isFocused;

    @Shadow
    private int selectionEnd;

    /** other selection position, maybe the same as the cursor */
    @Shadow
    private int enabledColor = 14737632;
    @Shadow
    private int disabledColor = 7368816;

    /** True if this textbox is visible */
    @Shadow
    private boolean visible = true;
    /**
     * @author Kiras
     */
    @Overwrite
    public void drawTextBox() {
        if (this.visible)
        {
            int color = new Color(0,0,0,60).getRGB();
            if (this.enableBackgroundDrawing)
            {
                Gui.drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, color);
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, color);
//                RenderUtils.drawBorderRect(this.xPosition - 1, this.yPosition - 1,this.yPosition + this.width + 1, this.yPosition + this.height + 1, 4,new Color(255,255,255,255).getRGB());
            }

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width);
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (s.length() > 0)
            {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = fontRendererInstance.drawString(s1, (float)l, (float)i1, i, true);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.maxStringLength;
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length())
            {
                j1 = fontRendererInstance.drawStringWithShadow(s.substring(j), (float)j1, (float)i1, i);
            }

            if (flag1)
            {
                if (flag2)
                {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + fontRendererInstance.FONT_HEIGHT, -3092272);
                }
                else
                {
                    fontRendererInstance.drawStringWithShadow("_", (float)k1, (float)i1, i);
                }
            }

            if (k != j)
            {
                int l1 = l + fontRendererInstance.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + fontRendererInstance.FONT_HEIGHT);
            }
        }
    }
    @Shadow
    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {

    }

}

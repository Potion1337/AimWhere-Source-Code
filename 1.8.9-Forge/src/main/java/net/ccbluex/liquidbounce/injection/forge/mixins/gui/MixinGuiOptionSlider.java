package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

import java.awt.*;

@Mixin(GuiOptionSlider.class)
public class MixinGuiOptionSlider extends MixinGuiButton {
    @Shadow
    public boolean dragging;
    @Shadow
    private float sliderValue;
    @Shadow
    private GameSettings.Options options;
    protected float cut,alpha;
    /**
     * @author Kiras
     */
    @Overwrite
    protected void mouseDragged(Minecraft p_mouseDragged_1_, int p_mouseDragged_2_, int p_mouseDragged_3_) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(p_mouseDragged_2_ - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                float lvt_4_1_ = this.options.denormalizeValue(this.sliderValue);
                p_mouseDragged_1_.gameSettings.setOptionFloatValue(this.options, lvt_4_1_);
                this.sliderValue = this.options.normalizeValue(lvt_4_1_);
                this.displayString = p_mouseDragged_1_.gameSettings.getKeyBinding(this.options);
            }
            RenderUtils.drawRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition,this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 8,this.yPosition + this.height, new Color(100,100,100,200).getRGB());
        }
    }
}

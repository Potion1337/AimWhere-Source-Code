/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.viaversion.ViaVersion;
import me.kiras.aimwhere.viaversion.util.ProtocolUtils;
import me.kiras.aimwhere.viaversion.util.ProtocolSorter;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof;
import net.ccbluex.liquidbounce.ui.client.GuiAntiForge;
import net.ccbluex.liquidbounce.ui.client.tools.GuiTools;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.kiras.aimwhere.viaversion.ProtocolSelectorScreen;

import java.awt.*;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {
    private GuiButton bungeeCordSpoofButton;
    private double val = -5 * 27 / 94 + 28;
    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        buttonList.add(new GuiButton(1337, 325, 8, 98, 20, ProtocolUtils.getProtocolName(ViaVersion.clientSideVersion)));
        buttonList.add(new GuiButton(997, 5, 8, 98, 20, "AntiForge"));
        buttonList.add(bungeeCordSpoofButton = new GuiButton(998, 108, 8, 98, 20, "BungeeCord Spoof: " + (BungeeCordSpoof.enabled ? "On" : "Off")));
        buttonList.add(new GuiButton(999, width - 104, 8, 98, 20, "Tools"));
    }

//    @Inject(method = "drawScreen", at = @At("TAIL"))
//    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_, CallbackInfo callbackInfo) {
//        drawViaVersion(p_drawScreen_1_, p_drawScreen_2_);
//    }


    public void drawViaVersion(int mouseX, int mouseY) {
        final ScaledResolution sr = new ScaledResolution(this.mc);
        int startX = sr.getScaledWidth() - 2;
        int startY = 5;

        double inc = 1;
        double max = 1;
        double min = 28;
        double valn = 0;
        int longValue = ((startX - 6) - (startX - 100));
        RenderUtils.drawBorderedRect(startX - 100, startY, startX - 6, startY + 22, 1, new Color(0, 0, 0).getRGB(),
                new Color(68, 68, 68).getRGB());
//        RenderUtil.drawRect(startX + length - 85, startY, (startX + length - 85) + (longValue * (valn - min) / (max - min)), startY  + 22, new Color(255, 255, 255,255).getRGB());
        boolean hover = mouseX > startX - 105 && mouseX < startX + 5 && mouseY > startY && mouseY < startY + 22;
        if (hover) {
            RenderUtils.drawRect(startX - 100, startY, startX - 6, startY + 22, new Color(0, 0, 0, 100).getRGB());
            if (Mouse.isButtonDown(0)) {
                double valAbs = mouseX - (startX - 100);
                double perc = valAbs / ((longValue) * Math.max(Math.min(valn / max, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                double valRel = (max - min) * perc;
                val = min + valRel;
                val = Math.round(val * (1 / inc)) / (1 / inc);
                ViaVersion.clientSideVersion = ProtocolSorter.getProtocolVersions().get((int) val).getVersion();
            }
        }
        FontManager.array16.drawStringWithShadow(ProtocolUtils.getProtocolName(ViaVersion.clientSideVersion),
                startX - 110
                        - FontManager.array16.getStringWidth(ProtocolUtils.getProtocolName(ViaVersion.clientSideVersion)),
                startY + 7, -1);
        RenderUtils.drawBorderedRect((float)(startX - 103F + (longValue * (val - min) / (max - min))), startY + 1,
                (float)(startX - 97 + (longValue * (val - min) / (max - min))), startY + 22, 1,
                new Color(255, 255, 255).getRGB(), new Color(180, 180, 180).getRGB());
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch(button.id) {
            case 1337:
                mc.displayGuiScreen(new ProtocolSelectorScreen((GuiScreen) (Object) this));
                break;
            case 997:
                mc.displayGuiScreen(new GuiAntiForge((GuiScreen) (Object) this));
                break;
            case 998:
                BungeeCordSpoof.enabled = !BungeeCordSpoof.enabled;
                bungeeCordSpoofButton.displayString = "BungeeCord Spoof: " + (BungeeCordSpoof.enabled ? "On" : "Off");
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);
                break;
            case 999:
                mc.displayGuiScreen(new GuiTools((GuiScreen) (Object) this));
                break;
        }
    }
}
/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;
import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.utils.render.Colors;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
@SideOnly(Side.CLIENT)
public class HUD extends Module {
    private static class PotionData {
        final Potion potion;
        public PotionData(Potion potion) {
            this.potion = potion;
        }

        public float getAnimationX() {
            return animationX;
        }

        public Potion getPotion() {
            return potion;
        }

        public int getMaxTimer() {
            return maxTimer;
        }

        int maxTimer = 0;
        float animationX = 0;
    }
    private Map<Potion, Double> timerMap = new HashMap<>();
//    public final ListValue inventoryAnimationValue = ("InventoryAnimation", new String[]{"Translate","Smooth"},"Translate");
//    public final IntegerValue smoothSpeedValue = createIntValue("AnimationSpeed", 3,1,5);
    public final BoolValue potionStatusValue = new BoolValue("PotionStatus", true);
    public final BoolValue blackHotbarValue = new BoolValue("BlackHotbar", true);
    public final BoolValue inventoryParticle = new BoolValue("InventoryParticle", false);
    private final BoolValue blurValue = new BoolValue("Blur", false);
    public static final BoolValue fontChatValue = new BoolValue("FontChat", false);
    public final IntegerValue xPos = new IntegerValue("PosX", 100,-700,700);
    public final IntegerValue yPos = new IntegerValue("PosY", 100,-700,700);
    public final IntegerValue redValue = new IntegerValue("Red", 100,0,255);
    public final IntegerValue greenValue = new IntegerValue("Green", 100,0,255);
    public final IntegerValue blueValue = new IntegerValue("Blue", 255,0,255);
    public HUD() {
        setState(true);
    }
    private int x = 5;
    public void renderPotionStatus(final float n, final float n2) {
        this.x = 5;
        for (final PotionEffect p_getDurationString_0_ : mc.thePlayer.getActivePotionEffects()) {
            final Potion potion = Potion.potionTypes[p_getDurationString_0_.getPotionID()];
            final PotionData potionData = new PotionData(potion);
            String s = I18n.format(potion.getName());
            int int1;
            int int2;
            try {
                int1 = Integer.parseInt(Potion.getDurationString(p_getDurationString_0_).split(":")[0]);
                int2 = Integer.parseInt(Potion.getDurationString(p_getDurationString_0_).split(":")[1]);
            }
            catch (Exception ex) {
                int1 = 0;
                int2 = 0;
            }
            final double n3 = int1 * 60 + int2;
            if (potionData.maxTimer == 0 || n3 > potionData.maxTimer) {
                potionData.maxTimer = (int)n3;
            }
            float n4 = 0.0f;
            if (n3 >= 0.0) {
                n4 = (float)(n3 / (float)potionData.maxTimer * 100.0);
            }
            if (!this.timerMap.containsKey(potion)) {
                this.timerMap.put(potion, n3);
            }
            if (this.timerMap.get(potion) == 0.0 || n3 > this.timerMap.get(potion)) {
                this.timerMap.replace(potion, n3);
            }
            switch (p_getDurationString_0_.getAmplifier()) {
                case 1: {
                    s += " II";
                    break;
                }
                case 2: {
                    s += " III";
                    break;
                }
                case 3: {
                    s += " IV";
                    break;
                }
            }
            final int n6 = (int)(n2 - 31.0f - this.mc.fontRendererObj.FONT_HEIGHT + this.x + 5.0f);
            if (n4 <= 1.0f) {
                n4 = 2.0f;
            }
            potionData.animationX = (float) RenderUtils.getAnimationState(potionData.animationX, 1.2f * n4, Math.max(10.0f, Math.abs(potionData.animationX - 1.2f * n4) * 15.0f) * 0.3f);
            RenderUtils.drawRoundedRect(n - 130.0f, n2 - 40.0f + this.x, n - 10.0f, n2 - 10.0f + this.x, ClientUtils.reAlpha(Colors.WHITE.c, 0.6f), ClientUtils.reAlpha(Colors.WHITE.c, 0.6f));
            RenderUtils.drawRoundedRect(n - 130.0f, n2 - 40.0f + this.x, n - 130.0f + potionData.animationX, n2 - 10.0f + this.x, ClientUtils.reAlpha(Colors.WHITE.c, 0.2f), ClientUtils.reAlpha(Colors.WHITE.c, 0.2f));
            final float n7 = n2 - this.mc.fontRendererObj.FONT_HEIGHT + this.x - 18.0f;
            FontManager.Chinese16.drawString(s, n - 101.0f, n7 - this.mc.fontRendererObj.FONT_HEIGHT, new Color(47, 116, 253).getRGB());
            FontManager.tahoma16.drawString(Potion.getDurationString(p_getDurationString_0_), n - 101.0f, n7 + 4.0f, ClientUtils.reAlpha(new Color(Colors.GREY.c).darker().getRGB(), 0.8f));
            if (potion.hasStatusIcon()) {
                GlStateManager.pushMatrix();
                GL11.glDisable(2929);
                GL11.glEnable(3042);
                GL11.glDepthMask(false);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int statusIconIndex = potion.getStatusIconIndex();
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                mc.ingameGUI.drawTexturedModalRect(n - 124.0f, (float)(n6 + 1), statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glEnable(2929);
                GlStateManager.popMatrix();
            }
            this.x -= 35;
        }
    }
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final ScaledResolution scaled = new ScaledResolution(mc);
        int mcWidth = scaled.getScaledWidth();
        int mcHeight = scaled.getScaledHeight();
        if(potionStatusValue.get())
            renderPotionStatus(mcWidth,mcHeight);
//        int color = new Color(redValue.get(),greenValue.get(),blueValue.get()).getRGB();
//        String pos = "X:" + EnumChatFormatting.WHITE + Math.round(mc.thePlayer.posX) + " Y:" + EnumChatFormatting.WHITE + Math.round(mc.thePlayer.posY) + " Z:" + EnumChatFormatting.WHITE + Math.round(mc.thePlayer.posZ);
//        String fps = "FPS:" + EnumChatFormatting.WHITE + Minecraft.getDebugFPS();
//        int posX = mcWidth / 2 - 265;
//        int posY = mcHeight / 2 + 147;
//        GameFontRenderer font = Fonts.font35;
//        font.drawStringWithShadow("Blocks/sec:" + EnumChatFormatting.WHITE + MovementUtils.getBlockSpeed(mc.thePlayer),posX + font.getStringWidth(pos) + font.getStringWidth(fps) + 6,posY,color);
//        font.drawStringWithShadow(pos,
//                posX,posY,color);
//        font.drawStringWithShadow(fps,posX + 4 + font.getStringWidth(pos),posY, color);
        if (mc.currentScreen instanceof GuiHudDesigner)
            return;
        LiquidBounce.hud.render(false);
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        LiquidBounce.hud.update();
    }

    @EventTarget
    public void onKey(final KeyEvent event) {
        LiquidBounce.hud.handleKey('a', event.getKey());
    }

    @EventTarget(ignoreCondition = true)
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat || event.getGuiScreen() instanceof GuiHudDesigner))
            mc.entityRenderer.loadShader(new ResourceLocation("AimWhere/blur.json"));
        else if (mc.entityRenderer.getShaderGroup() != null &&
                mc.entityRenderer.getShaderGroup().getShaderGroupName().contains("AimWhere/blur.json"))
            mc.entityRenderer.stopUseShader();
    }
}

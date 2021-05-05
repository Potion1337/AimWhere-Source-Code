/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import com.google.common.collect.Lists;
import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.utils.other.AnimationTools;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.ChatEvent;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Iterator;
import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    @Final
    private List<String> sentMessages;
    @Shadow
    @Final
    private List<ChatLine> chatLines;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;
    private float percentComplete = 0.0f;
    private int newLines;
    private long prevMillis = -1L;
    public boolean configuring;
    private void updatePercentage(long diff) {
        if (percentComplete < 1.0f) {
            percentComplete += 0.004f * (float)diff;
        }
        percentComplete = AnimationTools.clamp(percentComplete, 0.0f, 1.0f);
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public void drawChat(int updateCounter) {
        if (this.configuring) {
            return;
        }
        if (prevMillis == -1L) {
            prevMillis = System.currentTimeMillis();
            return;
        }
        long current = System.currentTimeMillis();
        long diff = current - prevMillis;
        prevMillis = current;
        updatePercentage(diff);
        float t = percentComplete;
        float percent = 1.0f - (t -= 1.0f) * t * t * t;
        percent = AnimationTools.clamp(percent, 0.0f, 1.0f);
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.getLineCount();
            boolean flag = false;
            int j = 0;
            int k = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            if (k > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }
                float f1 = this.getChatScale();
                int l = MathHelper.ceiling_float_int(((float)this.getChatWidth() / f1));
                GlStateManager.pushMatrix();
                if (!this.isScrolled) {
                    GlStateManager.translate((2.0f + (float)0), (20.0f + (float)0 + (9.0f - 9.0f * percent) * f1), 0.0f);
                } else {
                    GlStateManager.translate((2.0f + (float)0), (20.0f + (float)0), 0.0f);
                }
                GlStateManager.scale(f1, f1, 1.0f);
                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    int j1;
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline == null || (j1 = updateCounter - chatline.getUpdatedCounter()) >= 200 && !flag) continue;
                    double d0 = (double)j1 / 200.0;
                    d0 = 1.0 - d0;
                    d0 *= 10.0;
                    d0 = MathHelper.clamp_double(d0, 0.0, 1.0);
                    d0 *= d0;
                    int l1 = (int)(255.0 * d0);
                    if (flag) {
                        l1 = 255;
                    }
                    l1 = (int)((float)l1 * f);
                    ++j;
                    if (l1 <= 3) continue;
                    int i2 = 0;
                    int j2 = -i1 * 9;
//                    RenderUtils.drawRect(i2, (j2 - 9), (i2 + l + 4), j2, (l1 / 2 << 24));
                    String s = chatline.getChatComponent().getFormattedText();
                    GlStateManager.enableBlend();
                    if (i1 <= newLines) {
                        this.drawString(s, (float)i2, (float)(j2 - 8), 16777215 + ((int)((float)l1 * percent) << 24));
                    } else {
                        this.drawString(s, (float)i2, (float)(j2 - 8), 16777215 + (l1 << 24));
                    }
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                if (flag) {
                    int k2 = this.getFont().FONT_HEIGHT;
//                    int k2 = HUD.fontChatValue.get() ? FontManager.Chinese17.FONT_HEIGHT : this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                    int l2 = k * k2 + k;
                    int i3 = j * k2 + j;
                    int j3 = this.scrollPos * i3 / k;
                    int k1 = i3 * i3 / l2;
                    if (l2 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        RenderUtils.drawRect(0, (-j3), 2, (-j3 - k1), (l3 + (k3 << 24)));
                        RenderUtils.drawRect(2, (-j3), 1, (-j3 - k1), (13421772 + (k3 << 24)));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }
    public FontRenderer getFont() {
        return HUD.fontChatValue.get() ? FontManager.Chinese17 : this.mc.fontRendererObj;
    }
    public void drawString(String text, float x, float y,int color) {
        if(HUD.fontChatValue.get())
            FontManager.Chinese17.drawStringForChat(text,x,y,color, true);
        else
            this.mc.fontRendererObj.drawStringWithShadow(text,x,y,color);
    }

    /**
     * @author Kiras
     */
    @Overwrite
    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public void printChatMessage(IChatComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
        percentComplete = 0.0f;
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        final String text = chatComponent.getUnformattedText();
        LogManager.getLogger().info("[AimWhere-Chat] " + text);
        ChatEvent chatEvent = new ChatEvent(text);
        LiquidBounce.eventManager.callEvent(chatEvent);
    }
    /**
     * @author Kiras
     */
    @Overwrite
    private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }
        int i = MathHelper.floor_float(((float)this.getChatWidth() / this.getChatScale()));
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, getFont(), false, false);
        boolean flag = this.getChatOpen();
        newLines = list.size() - 1;
        for (IChatComponent ichatcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.resetScroll(1);
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, chatLineId));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();
        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }
    public void resetScroll(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();
        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public IChatComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaleFactor();
        float f = this.getChatScale();
        int j = mouseX / i - 3;
        int k = mouseY / i - 27;
        j = MathHelper.floor_float(((float)j / f));
        k = MathHelper.floor_float(((float)k / f));
        if (j >= 0 && k >= 0) {
            int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (j <= MathHelper.floor_float(((float)this.getChatWidth() / this.getChatScale())) && k < this.getFont().FONT_HEIGHT * l + l) {
                int i1 = k / this.getFont().FONT_HEIGHT + this.scrollPos;
                if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                    ChatLine chatline = this.drawnChatLines.get(i1);
                    int j1 = 0;
                    for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
                        if (!(ichatcomponent instanceof ChatComponentText) || (j1 += this.getFont().getStringWidth(GuiUtilRenderComponents.func_178909_a((String)((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false))) <= j) continue;
                        return ichatcomponent;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public void deleteChatLine(int id) {
        this.drawnChatLines.removeIf(i -> i.getChatLineID() == id);
        this.chatLines.removeIf(i -> i.getChatLineID() == id);
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public static int calculateChatboxWidth(float scale) {
        int i = 320;
        int j = 40;
        return MathHelper.floor_float((scale * (float)(i - j) + (float)j));
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public static int calculateChatboxHeight(float scale) {
        int i = 180;
        int j = 20;
        return MathHelper.floor_float((scale * (float)(i - j) + (float)j));
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}

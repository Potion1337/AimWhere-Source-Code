package net.ccbluex.liquidbounce.injection.forge.mixins.gui;
import java.util.Set;

import me.kiras.aimwhere.utils.math.Translate;
import me.kiras.aimwhere.utils.render.AnimationUtil;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.features.module.modules.world.ChestStealer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends MixinGuiScreen
{
    /** A list of the players inventory slots */
    @Shadow
    public Container inventorySlots;

    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    @Shadow
    protected int guiLeft;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    @Shadow
    protected int guiTop;

    /** holds the slot currently hovered */
    @Shadow
    private Slot theSlot;

    /** Used when touchscreen is enabled. */
    @Shadow
    private boolean isRightMouseClick;

    /** Used when touchscreen is enabled */
    @Shadow
    private ItemStack draggedStack;
    @Shadow
    private int touchUpX;
    @Shadow
    private int touchUpY;
    @Shadow
    private Slot returningStackDestSlot;
    @Shadow
    private long returningStackTime;

    /** Used when touchscreen is enabled */
    @Shadow
    private ItemStack returningStack;
    @Shadow
    @Final
    protected Set<Slot> dragSplittingSlots;
    @Shadow
    protected boolean dragSplitting;
    @Shadow
    private int dragSplittingRemnant;

    private float animationPosition;

    @Inject(method = "initGui", at = @At("HEAD"))
    public void initGui(CallbackInfo callbackInfo) {
        animationPosition = 75;
    }

    /**
     * @author Kiras
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        final HUD hud = LiquidBounce.moduleManager.getModule(HUD.class);
        final ChestStealer chestStealer = LiquidBounce.moduleManager.getModule(ChestStealer.class);
        try {
            Minecraft mc = Minecraft.getMinecraft();
            GuiScreen guiScreen = mc.currentScreen;
            if(chestStealer.getState() && chestStealer.getSilentValue().get() && guiScreen instanceof GuiChest){
                mc.setIngameFocus();
                mc.currentScreen = guiScreen;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.animationPosition = AnimationUtil.moveUD(this.animationPosition, 0.0F, 0.1F, 0.1F);
        GlStateManager.rotate(this.animationPosition, 0.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, this.animationPosition, 0.0f);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        super.drawScreen(mouseX,mouseY,partialTicks);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)i, (float)j, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
        {
            Slot slot = this.inventorySlots.inventorySlots.get(i1);
            this.drawSlot(slot);

            if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered())
            {
                this.theSlot = slot;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                int j1 = slot.xDisplayPosition;
                int k1 = slot.yDisplayPosition;
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(j1, k1, j1 + 16, k1 + 16);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }

        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
        ItemStack itemstack = this.draggedStack == null ? inventoryplayer.getItemStack() : this.draggedStack;

        if (itemstack != null)
        {
            int j2 = 8;
            int k2 = this.draggedStack == null ? 8 : 16;
            String s = null;

            if (this.draggedStack != null && this.isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = MathHelper.ceiling_float_int((float)itemstack.stackSize / 2.0F);
            }
            else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = this.dragSplittingRemnant;

                if (itemstack.stackSize == 0)
                {
                    s = "" + EnumChatFormatting.YELLOW + "0";
                }
            }

            this.drawItemStack(itemstack, mouseX - i - j2, mouseY - j - k2, s);
        }

        if (this.returningStack != null)
        {
            float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

            if (f >= 1.0F)
            {
                f = 1.0F;
                this.returningStack = null;
            }

            int l2 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
            int i3 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
            int l1 = this.touchUpX + (int)((float)l2 * f);
            int i2 = this.touchUpY + (int)((float)i3 * f);
            this.drawItemStack(this.returningStack, l1, i2, null);
        }

        GlStateManager.popMatrix();

        if (inventoryplayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
        {
            ItemStack itemstack1 = this.theSlot.getStack();
            this.renderToolTip(itemstack1, mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    protected void drawGradientRect(int left, int top, int right, int bottom)
    {
        float f = (float)(-2130706433 >> 24 & 255) / 255.0F;
        float f1 = (float)(-2130706433 >> 16 & 255) / 255.0F;
        float f2 = (float)(-2130706433 >> 8 & 255) / 255.0F;
        float f3 = (float)(-2130706433 & 255) / 255.0F;
        float f4 = (float)(-2130706433 >> 24 & 255) / 255.0F;
        float f5 = (float)(-2130706433 >> 16 & 255) / 255.0F;
        float f6 = (float)(-2130706433 >> 8 & 255) / 255.0F;
        float f7 = (float)(-2130706433 & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 200.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 200.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 200.0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 200.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Render an ItemStack. Args : stack, x, y, format
     */
    @Shadow
    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    @Shadow
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    @Shadow
    protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);
    @Shadow
    private void drawSlot(Slot slotIn)
    {
    }

    /**
     * Returns if the passed mouse position is over the specified slot. Args : slot, mouseX, mouseY
     */
    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, mouseX, mouseY);
    }

    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int left, int top, int pointX, int pointY)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        pointX = pointX - i;
        pointY = pointY - j;
        return pointX >= left - 1 && pointX < left + 16 + 1 && pointY >= top - 1 && pointY < top + 16 + 1;
    }
}

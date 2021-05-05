package me.kiras.aimwhere.modules.render;


import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import me.kiras.aimwhere.utils.render.AnimationUtil;
import me.kiras.aimwhere.utils.render.Colors;
import me.kiras.aimwhere.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

@ModuleInfo(
        name = "TargetInfo",
        description = "Show the target.",
        category = ModuleCategory.RENDER
)
public class TargetHUD extends Module
{
    private ListValue modeValue = new ListValue("Mode", new String[]{"Normal","JavaScript","Astolfo","AimWhere","Exhibition"},"Exhibition");
    private BoolValue AlwaysShow = new BoolValue("AlwaysShow", true);
    private BoolValue Optimisations = new BoolValue("Optimisations", true);
    private BoolValue MustAttack = new BoolValue("MustAttack", true);
    private BoolValue ColorBreathing = new BoolValue("ColorBreathing", true);
    private BoolValue NameUsesComplementaryColor = new BoolValue("NameUsesComplementaryColor", true);
    private FloatValue AnimationSpeed = new FloatValue("AnimationSpeed",1.4f,0.5f,3f);
    private ListValue ComplementaryColor = new ListValue("ComplementaryColor", new String[]{"Red","Green","Blue","Aftery","Purple","White"}, "Green");
    double anima = 0;
    int animAlpha = 0;
    float anim = 75.0f;
    private double healthBarWidth;
    private double healthBarWidth2;
    private double hudHeight;
    public static double animation;
    private GameFontRenderer font1;
    public DecimalFormat format;
    private final TimerUtil animationStopwatch;
    double ani;
    public static IntegerValue hudx = new IntegerValue("X", 70, 0, 200);
    public static IntegerValue hudY = new IntegerValue("Y", 80, 0, 200);
    private static BoolValue black = new BoolValue("Black", true);
    EntityLivingBase target;
    double animations;
    double animations2;
    int right2;
    float astolfoHelathAnim = 0f;
    boolean startAnim, stopAnim;
    public EntityLivingBase lastEnt;
    double dropShadowSpacing = 0.7;
    int posX = 400;
    int animationSpeed = 10;
    float displayHealth = 0;
    boolean targetOnGround = true;
    String onGround;
    int color;
    int TargetDistance = 0;
    String Distance = "";
    int targetHurtTime = 0;
    int ColourAddition = 0;
    int ColourBreathingOffset = 0;
    boolean RightLeft = true;
    int mcHeight = 100;
    int mcWidth = 100;
    int currenthealth = 0;
    float maxhealth = 1;
    NetworkPlayerInfo playerInfo;
    String targetPing;
    int Purple = new Color(84,30,97).getRGB();
    int Blue = new Color(32,45,99).getRGB();
    int Green = new Color(45,214,45).getRGB();
    int Red = new Color(190,0,0).getRGB();
    int White = new Color(255,255,255).getRGB();
    int Aftery = new Color(255,160,255).getRGB();
    public TargetHUD() {
        this.font1 = Fonts.font35;
        this.animationStopwatch = new TimerUtil();
        this.ani = 0.0;
        this.animations = 0.0;
        this.animations2 = 0.0;
        this.right2 = new ScaledResolution(mc).getScaledWidth() + 10;
    }
    @EventTarget
    private void onUpdate(UpdateEvent event) {
        KillAura aura = LiquidBounce.moduleManager.getModule(KillAura.class);
        EntityLivingBase target = aura.getTarget();
        ScaledResolution scale = new ScaledResolution(mc);
        if(target != null) {
            onGround = target.onGround ? "OnGround" : "NoGround";
        }
        if(ColorBreathing.get() && !ComplementaryColor.get().equals("White")){
            if(RightLeft) {
                ColourAddition++;
            } else {
                ColourAddition--;
            }
            if(ColourAddition >= 30) {
                RightLeft = false;
            }
            if(ColourAddition <= 0){
                RightLeft = true;
            }
        }
        if(!ColorBreathing.get()) {
            ColourAddition = 0;
        }
        if(ComplementaryColor.get().equals("Aftery")) {
            ColourBreathingOffset = -ColourAddition;
        } else {
            ColourBreathingOffset = ColourAddition;
        }
        if(target != null){
            if(mc.thePlayer.ticksExisted % 40 == 0) {
                mcHeight = scale.getScaledHeight();
                mcWidth = scale.getScaledWidth();
                maxhealth = target.getMaxHealth();
                playerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
                targetPing = playerInfo == null ? "0 ms" : playerInfo.getResponseTime()+" ms";
            }
            if(mc.thePlayer.ticksExisted %5 == 0) {
                currenthealth = (int)target.getHealth();
            }
            if(mc.thePlayer.ticksExisted %2 == 0) {
                TargetDistance = (int)target.getDistanceToEntity(mc.thePlayer);
                Distance = "Distance " + TargetDistance;
                if(TargetDistance>10) {
                    Distance = "Distance 10+";
                }
            }
            targetHurtTime = target.hurtTime;
        }
        switch(ComplementaryColor.get()) {
            case "White":
                color = White;
                ColourAddition = 0;
                break;
            case "Red":
                color = Red;
                break;
            case "Green":
                color = Green;
                break;
            case "Blue":
                color = Blue;
                break;
            case "Aftery":
                color = Aftery;
                break;
            case "Purple":
                color = Purple;
                break;
            default:
                break;
        }
    }


    private void renderItemStack(ItemStack stack, int x) {
        mc.getRenderItem().zLevel = -150.0F;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, 20);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, 20);
    }

    private void renderArmor(EntityPlayer player) {
        ItemStack armourStack;
        ItemStack[] renderStack = player.inventory.armorInventory;
        int length = renderStack.length;
        int xOffset = 36;
        for (ItemStack aRenderStack : renderStack) {
            armourStack = aRenderStack;
        }
        if (player.getHeldItem() != null) {
            ItemStack stock = player.getHeldItem().copy();
            if (stock.hasEffect() && (stock.getItem() instanceof ItemTool || stock.getItem() instanceof ItemArmor)) {
                stock.stackSize = 1;
            }
            this.renderItemStack(stock, xOffset);
            xOffset += 16;
        }
        renderStack = player.inventory.armorInventory;
        for (int index = 3; index >= 0; --index) {
            armourStack = renderStack[index];
            if (armourStack == null) continue;
            this.renderItemStack(armourStack, xOffset);
            xOffset += 16;
        }
    }

    private int toPercent(int num,float total) {
        return(Math.round(num / total * 10000)/100);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        Random random = new Random();
        KillAura Killaura = LiquidBounce.moduleManager.getModule(KillAura.class);
        ScaledResolution res = new ScaledResolution(this.mc);
        EntityLivingBase target1 = Killaura.getTarget();
        int x = res.getScaledWidth() / 2 + 30;
        int y = res.getScaledHeight() / 2 + 30;
        int Custom = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB();
        if (target1 != this.lastEnt && target1 != null) {
            this.lastEnt = target1;
        }
        if (startAnim) {
            stopAnim = false;
        }
        if (animAlpha == 255 && Killaura.getTarget() == null) {
            stopAnim = true;
        }
        startAnim = Killaura.getTarget() != null;
        if (startAnim) {
            anim = AnimationUtil.mvoeUD(anim, 0.0f, 0.09f);
            if (animAlpha < 255) {
                animAlpha += 15;
            }
        }
        if (stopAnim) {
            anim = AnimationUtil.mvoeUD(anim, 75f, 0.09f);
            if (animAlpha > 0) {
                animAlpha -= 15;
            }
        }
        if (target1 == null && animAlpha < 255) {
            startAnim = false;
            stopAnim = true;
        }
        EntityLivingBase player = null;
        if (modeValue.get().equals("Normal")) {
            if (lastEnt != null) {
                player = lastEnt;
            }
        } else {
            player = target1;
        }
        switch (modeValue.get()) {
            case "Normal":
                if (player != null && animAlpha >= 135) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(anim, 0f, 0f);
                    RenderUtils.drawBorderedRect(x + 33.0f, y + 0.0f, x + 130.0f, y + 36.0f, 2,
                            new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0, 150).getRGB());
                    mc.fontRendererObj.drawStringWithShadow(player.getName(), x + 38.0f, y + 3.0f, -1);
                    BigDecimal bigDecimal = new BigDecimal(player.getHealth());
                    bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
                    double HEALTH = bigDecimal.doubleValue();
                    BigDecimal DT = new BigDecimal(mc.thePlayer.getDistanceToEntity(player));
                    DT = DT.setScale(1, RoundingMode.HALF_UP);
                    double Dis = DT.doubleValue();
                    double Food = mc.thePlayer.getFoodStats().getFoodLevel();
                    double Armor = mc.thePlayer.getTotalArmorValue();
                    final float health = player.getHealth();
                    final float[] fractions = {0.0f, 0.5f, 1.0f};
                    final Color[] colors = {Color.RED, Color.green, Color.GREEN};
                    final float progress = health / player.getMaxHealth();
                    final Color customColor = (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter()
                            : Color.RED;
                    double width = mc.fontRendererObj.getStringWidth(player.getName());
                    width = getIncremental(width, 10.0);
                    if (width < 50.0) {
                        width = 50.0;
                    }
                    final double healthLocation = width * progress;
                    if (this.anima < healthLocation + 1) {
                        this.anima++;
                    }
                    if (this.anima > healthLocation + 1) {
                        this.anima--;
                    }
                    RenderUtils.drawGradientSideways(x + 37.5, y + 13.5, x + 37.5 + anima, y + 15.5,
                            new Color(0, 20, 0).getRGB(), new Color(Custom).getRGB());
                    RenderUtils.drawBorderedRect(x + 37, y + 13.5f, x + 39.0f + (float) width, y + 15.5f, 0.5f, Colors.getColor(0, 0),
                            new Color(0, 0, 0).getRGB());
                    for (int i = 1; i < 10; ++i) {
                        final double dThing = width / 10.0 * i;
                    }
                    String COLOR1;
                    if (health > 20.0D) {
                        COLOR1 = " \2479";
                    } else if (health >= 10.0D) {
                        COLOR1 = " \247a";
                    } else if (health >= 3.0D) {
                        COLOR1 = " \247e";
                    } else {
                        COLOR1 = " \2474";
                    }
                    GlStateManager.scale(0.5f, 0.5f, 0.5f);
                    String str = "Dist >> §6" + Dis + " § HP >> §2" + health;
                    mc.fontRendererObj.drawStringWithShadow(str, x * 2 + 76.0f, y * 2 + 40.0f,
                            new Color(255, 255, 255).getRGB());
                    String str2 = String.format("Yaw >> §5%s", (int) player.rotationYaw + " § Armor >>§3 " + Armor);
                    mc.fontRendererObj.drawStringWithShadow(str2, x * 2 + 76.0f, y * 2 + 55.0f,
                            new Color(255, 255, 255).getRGB());
                    GlStateManager.popMatrix();
                }
                break;
            case "Astolfo":
                if (player != null) {
                    RenderUtils.pre();
                    GlStateManager.pushMatrix();

                    // BaseRect(black)
                    RenderUtils.drawRect(x + 0.7f, y, x + 149.7f, y + 60, new Color(0, 0, 0, 110).getRGB());

                    // health color math
                    float health = player.getHealth();
                    float health2;
                    float[] fractions = new float[]{0.0f, 0.2f, 0.7f};
                    Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                    float progress = health / player.getMaxHealth();
                    Color customColor = health >= 0.0f ? blendColors(fractions, colors, progress).brighter()
                            : Color.RED;

                    customColor = customColor.darker();

                    // Player name
                    Fonts.font35.drawStringWithShadow(player.getName(), x + 37, y + 8, -1);
                    //Fonts.font35.drawStringWithShadow("Dist: " + (int)mc.thePlayer.getDistanceToEntity(player), x + 37, y + 35, -1);


                    // Player Health

                    GlStateManager.scale(1.5f, 1.5f, 1.5f);
                    Fonts.font35.drawString(Math.round(player.getHealth()) + "❤", (x + 37) / 1.5f,
                            (y + 20) / 1.5f, customColor.getRGB());
                    // health rect animation
                    double wdnmd = 150D;
                    if (this.astolfoHelathAnim < wdnmd
                            * (health2 = player.getHealth() / player.getMaxHealth())) {
                        if (wdnmd * health2 - this.astolfoHelathAnim < 1.0) {
                            this.astolfoHelathAnim = (float) (wdnmd * health2);
                        }
                        this.astolfoHelathAnim = (float) (this.astolfoHelathAnim + 4D);
                    }
                    if (wdnmd * health2 - this.astolfoHelathAnim > 1.0) {
                        this.astolfoHelathAnim = (float) (wdnmd * health2);
                    }
                    this.astolfoHelathAnim = (float) (this.astolfoHelathAnim - 4D);
                    if (astolfoHelathAnim < 0) {
                        astolfoHelathAnim = 0;
                    }

                    // health rect base
                    RenderUtils.drawRect((x + 2.985f) / 1.5f, (y + 55) / 1.5f, (x + 148) / 1.5f, (y + 58) / 1.5f,
                            new Color(customColor.getRed(), customColor.getGreen(), customColor.getBlue(), 100).getRGB());

                    // health rect main
                    RenderUtils.drawRect((x + 2.985f) / 1.5f, (y + 55) / 1.5f, (x + 2 + (astolfoHelathAnim)) / 1.5f,
                            (y + 58) / 1.5f, customColor.getRGB());

                    GlStateManager.popMatrix();
                    RenderUtils.post();

                    // Player Model
                    GlStateManager.color(1.0f, 1.0f, 1.0f);
                    RenderUtils.drawEntityOnScreen(x + 18,
                            (int) (y + (player.isSneaking() ? 38
                                    : ((player instanceof EntityPlayer)
                                    ? (((EntityPlayer) player).inventory.armorInventory.length == 0 ? 44 : 46)
                                    : 46))
                                    + (player.getEyeHeight() / 0.3f)),
                            24, player.rotationYaw, player.rotationPitch, player);

                    if (player instanceof EntityPlayer) {
                        GlStateManager.pushMatrix();
                        // Player Armor
                        EntityPlayer entityplayer = (EntityPlayer) player;
                        ArrayList<ItemStack> stuff = new ArrayList<>();
                        int split = x + 132;
                        int y2 = y - 16;
                        int index = 3;
                        while (index >= 0) {
                            ItemStack armer = entityplayer.inventory.armorInventory[index];
                            if (armer != null) {
                                stuff.add(armer);
                            }
                            --index;
                        }
                        for (ItemStack errything : stuff) {
                            if (mc.theWorld != null) {
                                RenderHelper.enableGUIStandardItemLighting();
                                y2 += 14;
                            }
                            GlStateManager.disableAlpha();
                            GlStateManager.clear(256);
                            mc.getRenderItem().zLevel = -150.0f;
                            mc.getRenderItem().renderItemAndEffectIntoGUI(errything, split, y2);
                            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, errything, split, y2);
                            mc.getRenderItem().zLevel = 0.0f;
                            GlStateManager.disableBlend();
                            GlStateManager.scale(0.5, 0.5, 0.5);
                            GlStateManager.disableDepth();
                            GlStateManager.disableLighting();
                            GlStateManager.enableDepth();
                            GlStateManager.scale(2.0f, 2.0f, 2.0f);
                            GlStateManager.enableAlpha();
                        }
                        GlStateManager.popMatrix();
                    }
                }
                break;
            case "JavaScript":
                float PartialTicks = 1 - event.getPartialTicks();
                KillAura aura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
                EntityLivingBase target = aura.getTarget();
                if (target != null) {
                    if (target.isDead || mc.thePlayer.getDistanceToEntity(target) >= 10) {
                        target = null;
                    }
                    animationSpeed = (int) (posX / 10 + 0.0005);
                    if (MustAttack.get() && mc.thePlayer.isSwingInProgress || !MustAttack.get() && target != null && target.getDistanceToEntity(mc.thePlayer) < 7 && !target.isDead || AlwaysShow.get()) {
                        if (posX > 0) {
                            posX = (int) (posX - animationSpeed * AnimationSpeed.get() * PartialTicks);
                        }
                    } else if (!AlwaysShow.get()) {
                        if (posX < mcWidth) {
                            posX = (int) (posX + animationSpeed * 1.3 * AnimationSpeed.get() * PartialTicks);
                        }
                    }
                    float offSetWidth = mcWidth / 2 + 46 + posX;
                    float offSetHeight = mcHeight / 2 + 40;
                    if (displayHealth - 1 > (toPercent(currenthealth, maxhealth) * 1.4)) {
                        displayHealth = displayHealth - 1 * AnimationSpeed.get() * PartialTicks;
                    } else if (displayHealth + 1 < (toPercent(currenthealth, maxhealth) * 1.4)) {
                        displayHealth = displayHealth + 1 * AnimationSpeed.get() * PartialTicks;
                    }
                    RenderUtils.drawRect(offSetWidth, offSetHeight, mcWidth / 2 + 200 + posX, mcHeight / 2 + 100, 0xC0000000);
                    for (int i = 1; i < 5; i++) {
                        if (posX < mcWidth) {
                            RenderUtils.drawRect(offSetWidth - (float) dropShadowSpacing * i, offSetHeight - (float) dropShadowSpacing * i, mcWidth / 2 + 200 + posX + (float) dropShadowSpacing * i, mcHeight / 2 + 100 + (float) dropShadowSpacing * i, 0x1A000000);
                        } else if (!Optimisations.get()) {
                            RenderUtils.drawRect(offSetWidth - (float) dropShadowSpacing * i, offSetHeight - (float) dropShadowSpacing * i, mcWidth / 2 + 200 + posX + (float) dropShadowSpacing * i, mcHeight / 2 + 100 + (float) dropShadowSpacing * i, 0x1A000000);
                        }
                    }
                    if (NameUsesComplementaryColor.get()) {
                        Fonts.font40.drawString(target.getName(), offSetWidth + 5, offSetHeight + 5, color + ColourBreathingOffset * 5);
                    } else {
                        Fonts.font40.drawString(target.getName(), offSetWidth + 5, offSetHeight + 5, White);
                    }
                    drawEntityOnScreen(offSetWidth + 14, offSetHeight + 55, 20.3f, target.rotationYaw, target.rotationPitch, target);
                    RenderUtils.drawRect(offSetWidth + 33, offSetHeight + 50, offSetWidth + displayHealth, offSetHeight + 4 + 50, color + ColourBreathingOffset * 5);
                    Fonts.font40.drawString(Distance + "  |  Hurt " + targetHurtTime, offSetWidth + 33, offSetHeight + 18, White);
                    Fonts.font40.drawString("Ping " + targetPing, offSetWidth + 33, offSetHeight + 30, White);
                }
                break;
            case "AimWhere": {
                KillAura killaura = LiquidBounce.moduleManager.getModule(KillAura.class);
                EntityLivingBase entity = killaura.getTarget();
                if (entity != null && !entity.isDead && entity instanceof EntityPlayer && mc.thePlayer.getDistanceToEntity(entity) < 8.0F) {
                    double hpPercentage = entity.getHealth() / entity.getMaxHealth();
                    ScaledResolution scaledRes = new ScaledResolution(mc);
                    float scaledWidth = (float) scaledRes.getScaledWidth();
                    float scaledHeight = (float) scaledRes.getScaledHeight();
                    if (hpPercentage > 1.0D) {
                        hpPercentage = 1.0D;
                    } else if (hpPercentage < 0.0D) {
                        hpPercentage = 0.0D;
                    }
                    RenderUtils.rectangleBordered((scaledWidth / 2.0F - 200.0F), (scaledHeight / 2.0F - 42.0F), (scaledWidth / 2.0F - 200.0F + 40.0F), (scaledHeight / 2.0F - 2.0F), 1.0D, Colors.getColor(0, 0), Colors.getColor(0, 0));
                    RenderUtils.drawRect(scaledWidth / 2.0F - 200.0F, scaledHeight / 2.0F - 42.0F, scaledWidth / 2.0F - 200.0F + 40.0F + (float) (mc.fontRendererObj.getStringWidth(entity.getName()) > 105 ? mc.fontRendererObj.getStringWidth(entity.getName()) - 10 : 105), scaledHeight / 2.0F - 2.0F, (new Color(34, 34, 34, 150)).getRGB());
                    RenderUtils.drawFace((int) scaledWidth / 2 - 196, (int) (scaledHeight / 2.0F - 38.0F), 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F, (AbstractClientPlayer) entity);
                    mc.fontRendererObj.drawStringWithShadow(entity.getName(), scaledWidth / 2.0F - 196.0F + 40.0F, scaledHeight / 2.0F - 36.0F, -1);
                    RenderUtils.drawRect((scaledWidth / 2.0F - 196.0F + 40.0F), (scaledHeight / 2.0F - 26.0F), (double) (scaledWidth / 2.0F - 196.0F + 40.0F) + 87.5D, (scaledHeight / 2.0F - 14.0F), (new Color(55, 55, 55)).getRGB());
                    RenderUtils.drawRect((scaledWidth / 2.0F - 196.0F + 40.0F), (scaledHeight / 2.0F - 26.0F), (double) (scaledWidth / 2.0F - 196.0F + 40.0F) + hpPercentage * 1.25D * 70.0D, (scaledHeight / 2.0F - 14.0F), Colors.getHealthColor(entity).getRGB());
                    mc.fontRendererObj.drawStringWithShadow(String.format("%.1f", entity.getHealth()), scaledWidth / 2.0F - 196.0F + 40.0F + 36.0F, scaledHeight / 2.0F - 23.0F, Colors.getHealthColor(entity).getRGB());
                    mc.fontRendererObj.drawStringWithShadow("Distance: \u00a77" + (int) mc.thePlayer.getDistanceToEntity(entity) + "m", scaledWidth / 2.0F - 196.0F + 40.0F, scaledHeight / 2.0F - 12.0F, -1);
                }
                break;
            }
            case "Exhibition": {
                EntityLivingBase entity = LiquidBounce.moduleManager.getModule(KillAura.class).getTarget();
                ScaledResolution scaledRes = new ScaledResolution(mc);
                float width = (float)scaledRes.getScaledWidth();
                float height = (float)scaledRes.getScaledHeight();
                if(entity instanceof EntityPlayer && !entity.isDead && mc.thePlayer.getDistanceToEntity(entity) < 8.0F) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(width / 2.0F + 10.0F, height - 90.0F, 0.0F);
                    //RenderUtils.rectangle(0.0D, 0.0D, 125.0D, 36.0D, Colors.getColor(0, 150));
                    RenderUtils.autoExhibition(0F,-2F,125F,38F,1F);
                    mc.fontRendererObj.drawStringWithShadow(entity.getName(), 38.0F, 2.0F, -1);
                    float health = entity.getHealth();
                    float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
                    Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                    float progress = health / entity.getMaxHealth();
                    Color customColor = entity.hurtTime > 5?Color.RED:(health >= 0.0F?blendColors(fractions, colors, progress).brighter():Color.RED);
                    double width1 = mc.fontRendererObj.getStringWidth(entity.getName());
                    width1 = getIncremental(width1, 10.0D);
                    if(width1 < 50.0D) {
                        width1 = 50.0D;
                    }

                    double healthLocation = width1 * (double)progress;
                    RenderUtils.rectangle(37.5D, 11.5D, 38.0D + healthLocation + 0.5D, 14.5D, customColor.getRGB());
                    RenderUtils.drawRectBordered(37.0D, 11.0D, 39.0D + width1, 15.0D, 0.5D, Colors.getColor(0, 0), Colors.getColor(0));

                    for(int dist = 1; dist < 10; ++dist) {
                        double dThing = width1 / 10.0D * (double)dist;
                        RenderUtils.rectangle(38.0D + dThing, 11.0D, 38.0D + dThing + 0.5D, 15.0D, Colors.getColor(0));
                    }

                    GlStateManager.scale(0.5D, 0.5D, 0.5D);
                    int var18 = (int)mc.thePlayer.getDistanceToEntity(entity);
                    String str = "HP: " + (int)health + " | Dist: " + var18;
                    mc.fontRendererObj.drawStringWithShadow(str, 76.0F, 35.0F, -1);
                    GlStateManager.scale(2.0F, 2.0F, 2.0F);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    this.renderArmor((EntityPlayer)entity);
                    RenderUtils.drawEntityOnScreen(18, 34, 16.0F, 0.0F, 9.0F, entity);
                    GlStateManager.popMatrix();
                }

            }
        }
    }

    @Override
    public void onEnable() {
        animAlpha = 0;
        startAnim = false;
        stopAnim = false;
        astolfoHelathAnim = 0f;
    }

    private double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static int[] getFractionIndicies(final float[] fractions, final float progress) {
        final int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        Color color;
        if (fractions.length == colors.length) {
            final int[] indicies = getFractionIndicies(fractions, progress);
            final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
            final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
            final float max = range[1] - range[0];
            final float value = progress - range[0];
            final float weight = value / max;
            color = blend(colorRange[0], colorRange[1], 1.0f - weight);
            return color;
        }
        return new Color(255,255,255);
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float) ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            final NumberFormat nf = NumberFormat.getNumberInstance();
            exp.printStackTrace();
        }
        return color3;
    }

    private void drawEntityOnScreen(float posX,float posY,float scale,float mouseX,float mouseY,EntityLivingBase ent){
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX,posY,50.0);
        GlStateManager.scale((-scale),scale,scale);
        GlStateManager.rotate(180,0,0,1);
        GlStateManager.rotate(135,0,1,0);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135,0,1,0);
        GlStateManager.translate(0,0,0.0);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent,0.0,0.0,0.0,0,1);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}

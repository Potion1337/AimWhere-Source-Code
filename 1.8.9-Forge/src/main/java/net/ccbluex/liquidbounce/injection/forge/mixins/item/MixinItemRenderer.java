
package net.ccbluex.liquidbounce.injection.forge.mixins.item;
import me.kiras.aimwhere.modules.render.Animation;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemRenderer {
    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);

    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);
    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    private void x3IsBlack(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F) * -35.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }
    private boolean use(ItemStack itemToRender) {
        boolean flag = Animation.leftHand.get();
        return (itemToRender == null || itemToRender.getItem() == null || !(itemToRender.getItem() instanceof ItemMap)) && (!Animation.leftHand.get() ? flag : (itemToRender == null ? flag : (itemToRender.getItem() == null ? flag : (itemToRender.getItem() instanceof ItemBow ? !flag : flag))));
    }

    private void circle() {
        this.ticks+=2;
        GlStateManager.translate(0.7F, -0.4F, -0.8F);
        GlStateManager.rotate((float) this.ticks * 0.2F * (Animation.Speed.get()).floatValue(), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(34.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4D, 0.4D, 0.4D);
    }


    /**
     * @author CCBlueX
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        float f1 = abstractclientplayer.getSwingProgress(partialTicks);
        float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
        float var2 = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        float var22 = mc.thePlayer.getSwingProgress(partialTicks);
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        if (use(this.itemToRender))
            GlStateManager.disableCull();
         else
            GlStateManager.enableCull();
        if(this.itemToRender != null) {
            final KillAura aura = LiquidBounce.moduleManager.getModule(KillAura.class);
            GlStateManager.translate(Animation.itemPosX.get(),Animation.itemPosY.get(),Animation.itemPosZ.get());
            if(this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0 || (itemToRender.getItem() instanceof ItemSword && aura.getBlockingStatus())) {
                EnumAction enumaction = aura.getBlockingStatus() ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();
                switch (enumaction) {
                    case NONE:
                        transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(abstractclientplayer, partialTicks);
                        this.transformFirstPersonItem(f, f1);
                        break;
                    case BLOCK:
                        Animation blockAnimations = LiquidBounce.moduleManager.getModule(Animation.class);
                        if (blockAnimations.getState()) {
                            switch (Animation.mode.get()) {
                                case "White":
                                    this.func_178096_b(0.0F, 0.0F);
                                    this.func_178103_d();
                                    int alpha1 = (int)Math.min(255L, (System.currentTimeMillis() % 255L > 127L?Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L):System.currentTimeMillis() % 255L) * 2L);
                                    GlStateManager.translate(0.5F, -0.1F, 0.8F);
                                    GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
                                    GlStateManager.translate(0.0F, 0.5F, 0.0F);
                                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, -1.0F);
                                    GlStateManager.translate(0.6F, 0.5F, 0.0F);
                                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, -1.0F);
                                    GlStateManager.rotate(-10.0F, 1.0F, 0.0F, -1.0F);
                                    GlStateManager.rotate(this.mc.thePlayer.isSwingInProgress ? (-alpha1 / 4.0f) : 1.0f, 1.0f, -0.0f, 1.0f);
                                    GlStateManager.scale(1.5F, 1.5F, 1.5F);
                                    break;
                                case "Swaing":
                                    float af = 1.0F
                                            - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
                                    EntityPlayerSP aentityplayersp = mc.thePlayer;
                                    float af1 = aentityplayersp.getSwingProgress(partialTicks);
                                    GL11.glTranslated(-0.1F, 0.15F, 0.0F);
                                    GL11.glTranslated(0.1F, -0.2F, 0.0F);
                                    this.avatar(af, af1);
                                    this.doBlockTransformations();
                                    break;

                                case "Winter":
                                    this.x3IsBlack(-0.3F, f1);
                                    this.doBlockTransformations();
                                    break;
                                case "Spin":
                                    Random2();
                                    this.func_178103_d();
                                    break;
                                case "Slide":
                                    this.tap(f, f1);
                                    doBlockTransformations();
                                    break;
                                case "Sigma":
                                    transformFirstPersonItem(f, 0.0F);
                                    doBlockTransformations();
                                    float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.translate(-0.0F, 0.4F, 1.0F);
                                    GlStateManager.rotate(-f4 * 22.5F, -9.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-f4 * 10.0F, 1.0F, -0.4F, -0.5F);
                                    break;
                                case "NoSwing":
                                    transformFirstPersonItem(f, 0.0F);
                                    doBlockTransformations();
                                    break;
                                case "Light":
                                    transformFirstPersonItem(f, 0.0F);
                                    doBlockTransformations();
                                    GlStateManager.scale(0.54F, 0.54F, 0.54F);
                                    GlStateManager.translate(-0.4F, 1F, 0.4F);
                                    GlStateManager.rotate(-var15 * 90.0F, -15.0F, -15.0F, 19.0F);
                                    break;
                                case "Swang":
                                    transformFirstPersonItem(f / 2.0F, f1);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 30.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 40.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                case "Swank":
                                    transformFirstPersonItem(f / 2.0F, f1);
                                    float Swank = MathHelper.sin(MathHelper.sqrt_float(f) * 3.1415927F);
                                    GlStateManager.rotate(Swank * 30.0F, -Swank, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swank * 40.0F, 1.0F, -Swank, -0.0F);
                                    doBlockTransformations();
                                    break;
                                case "Swong":
                                    transformFirstPersonItem(f / 2.0F, 0.0F);
                                    float Swong = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(-Swong * 40.0F / 2.0F, Swong / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-Swong * 30.0F, 1.0F, Swong / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                case "Jigsaw":
                                    transformFirstPersonItem(0.1F, f1);
                                    doBlockTransformations();
                                    GlStateManager.translate(-0.5D, 0.0D, 0.0D);
                                    break;
                                case "Old":
                                    genCustom(f, f1);
                                    doBlockTransformations();
                                    break;
                                case "Luna":
                                    transformFirstPersonItem(f, 0.0F);
                                    doBlockTransformations();
                                    float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.scale(1.0F, 1.0F, 1.0F);
                                    GlStateManager.translate(-0.2F, 0.45F, 0.25F);
                                    GlStateManager.rotate(-sin * 20.0F, -5.0F, -5.0F, 9.0F);
                                    break;
                                case "Jello":
                                    transformFirstPersonItem(0.0F, 0.0F);
                                    doBlockTransformations();
                                    int alpha = (int)Math.min(255L, ((System.currentTimeMillis() % 255L > 127L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : (System.currentTimeMillis() % 255L)) * 2L);
                                    GlStateManager.translate(0.3F, -0.0F, 0.4F);
                                    GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
                                    GlStateManager.translate(0.0F, 0.5F, 0.0F);
                                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, -1.0F);
                                    GlStateManager.translate(0.6F, 0.5F, 0.0F);
                                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, -1.0F);
                                    GlStateManager.rotate(-10.0F, 1.0F, 0.0F, -1.0F);
                                    GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? (-alpha / 5.0F) : 1.0F, 1.0F, -0.0F, 1.0F);
                                    break;
                                case "Circle":
                                    circle();
                                    break;
                                case "ETB":
                                    this.ETB(f, f1);
                                    this.doBlockTransformations();
                                    break;
                                case "Avatar":
                                    this.avatar(f, f1);
                                    this.doBlockTransformations();
                                    break;
                                case "IDBUG":
                                    this.IDBUG(f, f1);
                                    this.doBlockTransformations();
                                    break;
                                case "NoSword":
                                    this.transformFirstPersonItem(f2, 0.0F);
                                    this.doBlockTransformations();
                                    float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415926535F);
                                    GlStateManager.rotate(-var91 * 70.0F / 2.0F, -2.0F, -0.0F, 2.0F);
                                    GlStateManager.rotate(-var91 * 70.0F, 1.0F, -0.4F, -0.0F);
                                    break;
                                case "Normal":
                                    this.genCustom(0.0F, 0.0F);
                                    this.doBlockTransformations();
                                    float var1 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.translate(-0.5F, 0.4F, 0.0F);
                                    GlStateManager.rotate(-var1 * 50.0F, -8.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-var1 * 70.0F, 1.0F, -0.4F, -0.0F);
                                    break;
                                case "Custom":
                                    this.doBlockTransformations();
                                    float var0 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.translate(-0.5F, 0.4F, 0.0F);
                                    GlStateManager.rotate(-var0 * 50.0F, -8.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-var0 * 70.0F, 1.0F, -0.4F, -0.0F);
                                    break;
                                case "Rotate":
                                    Random();
                                    this.doBlockTransformations();
                                    break;
                                case "Vanilla":
                                    this.Hietiens(f, f1);
                                    this.func_178103_d();
                                    break;
                                case "Remix":
                                    this.func_178096_b(f, 0.83f);
                                    this.func_178103_d();
                                    float f5 = MathHelper.sin((MathHelper.sqrt_float(f1) * 3.83f));
                                    GlStateManager.translate(-0.5f,0.2f, 0.2f);
                                    GlStateManager.rotate((-f5 * 0.0f), 0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate((-f5 * 43.0f), 58.0f, 23.0f, 45.0f);
                                    break;
                                case "Leain":
                                    this.func_178096_b(var2 * 0.5f, 0);
                                    GlStateManager.rotate(-var15 * 25 / 2.0F, -18.0F, -0.0F, 9.0F);
                                    this.func_178103_d();
                                    GL11.glTranslated(1.5, 0.3, 0.5);
                                    GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                                    break;
                            }
                            break;
                        }
                        transformFirstPersonItem(f + 0.1F, f1);
                        doBlockTransformations();
                        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                        break;
                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                        break;
                }
            } else {
                Animation Ani = LiquidBounce.moduleManager.getModule(Animation.class);
                if (Animation.EveryThingBlock.get() && this.mc.gameSettings.keyBindUseItem.isKeyDown() && Ani.getState()) {
                    switch (Animation.mode.get()) {
                        case "White":
                            this.func_178096_b(0.0F, 0.0F);
                            this.func_178103_d();
                            int alpha = (int)Math.min(255L, (System.currentTimeMillis() % 255L > 127L?Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L):System.currentTimeMillis() % 255L) * 2L);
                            GlStateManager.translate(0.5F, -0.1F, 0.8F);
                            GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
                            GlStateManager.translate(0.0F, 0.5F, 0.0F);
                            GlStateManager.rotate(90.0F, 1.0F, 0.0F, -1.0F);
                            GlStateManager.translate(0.6F, 0.5F, 0.0F);
                            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, -1.0F);
                            GlStateManager.rotate(-10.0F, 1.0F, 0.0F, -1.0F);
                            GlStateManager.rotate(this.mc.thePlayer.isSwingInProgress ? (-alpha / 4.0f) : 1.0f, 1.0f, -0.0f, 1.0f);
                            GlStateManager.scale(1.5F, 1.5F, 1.5F);
                            break;
                        case "Swaing":
                            float af = 1.0F
                                    - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
                            EntityPlayerSP aentityplayersp = mc.thePlayer;
                            float af1 = aentityplayersp.getSwingProgress(partialTicks);
                            GL11.glTranslated(-0.1F, 0.15F, 0.0F);
                            GL11.glTranslated(0.1F, -0.2F, 0.0F);
                            this.avatar(af, af1);
                            this.doBlockTransformations();
                            break;
                        case "Winter":
                            this.x3IsBlack(-0.3F, f1);
                            this.doBlockTransformations();
                            break;
                        case "Spin":
                            Random2();
                            this.func_178103_d();
                            break;
                        case "Slide":
                            this.tap(f, f1);
                            doBlockTransformations();
                            break;
                        case "Sigma":
                            transformFirstPersonItem(f, 0.0F);
                            doBlockTransformations();
                            float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            GlStateManager.translate(-0.0F, 0.4F, 1.0F);
                            GlStateManager.rotate(-f4 * 22.5F, -9.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-f4 * 10.0F, 1.0F, -0.4F, -0.5F);
                            break;
                        case "NoSwing":
                            transformFirstPersonItem(f, 0.0F);
                            doBlockTransformations();
                            break;
                        case "Light":
                            transformFirstPersonItem(f, 0.0F);
                            doBlockTransformations();
                            GlStateManager.scale(0.54F, 0.54F, 0.54F);
                            GlStateManager.translate(-0.4F, 1F, 0.4F);
                            GlStateManager.rotate(-var15 * 90.0F, -15.0F, -15.0F, 19.0F);
                            break;
                        case "Swang":
                            transformFirstPersonItem(f / 2.0F, f1);
                            float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            GlStateManager.rotate(Swang * 30.0F / 2.0F, -Swang, -0.0F, 9.0F);
                            GlStateManager.rotate(Swang * 40.0F, 1.0F, -Swang / 2.0F, -0.0F);
                            doBlockTransformations();
                            break;
                        case "Swank":
                            transformFirstPersonItem(f / 2.0F, f1);
                            float Swank = MathHelper.sin(MathHelper.sqrt_float(f) * 3.1415927F);
                            GlStateManager.rotate(Swank * 30.0F, -Swank, -0.0F, 9.0F);
                            GlStateManager.rotate(Swank * 40.0F, 1.0F, -Swank, -0.0F);
                            doBlockTransformations();
                            break;
                        case "Swong":
                            transformFirstPersonItem(f / 2.0F, 0.0F);
                            float Swong = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            GlStateManager.rotate(-Swong * 40.0F / 2.0F, Swong / 2.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-Swong * 30.0F, 1.0F, Swong / 2.0F, -0.0F);
                            doBlockTransformations();
                            break;
                        case "Jigsaw":
                            transformFirstPersonItem(0.1F, f1);
                            doBlockTransformations();
                            GlStateManager.translate(-0.5D, 0.0D, 0.0D);
                            break;
                        case "Old":
                            genCustom(f, f1);
                            doBlockTransformations();
                            break;
                        case "Luna":
                            transformFirstPersonItem(f, 0.0F);
                            doBlockTransformations();
                            float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            GlStateManager.scale(1.0F, 1.0F, 1.0F);
                            GlStateManager.translate(-0.2F, 0.45F, 0.25F);
                            GlStateManager.rotate(-sin * 20.0F, -5.0F, -5.0F, 9.0F);
                            break;
                        case "Jello":
                            transformFirstPersonItem(0.0F, 0.0F);
                            doBlockTransformations();
                            int alpha1 = (int)Math.min(255L, ((System.currentTimeMillis() % 255L > 127L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : (System.currentTimeMillis() % 255L)) * 2L);
                            GlStateManager.translate(0.3F, -0.0F, 0.4F);
                            GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
                            GlStateManager.translate(0.0F, 0.5F, 0.0F);
                            GlStateManager.rotate(90.0F, 1.0F, 0.0F, -1.0F);
                            GlStateManager.translate(0.6F, 0.5F, 0.0F);
                            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, -1.0F);
                            GlStateManager.rotate(-10.0F, 1.0F, 0.0F, -1.0F);
                            GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? (-alpha1 / 5.0F) : 1.0F, 1.0F, -0.0F, 1.0F);
                            break;
                        case "360°":
                            circle();
                            break;
                        case "ETB":
                            this.ETB(f, f1);
                            this.doBlockTransformations();
                            break;
                        case "Avatar":
                            this.avatar(f, f1);
                            this.doBlockTransformations();
                            break;
                        case "IDBUG":
                            this.IDBUG(f, f1);
                            this.doBlockTransformations();
                            break;
                        case "NoSword":
                            this.transformFirstPersonItem(f2, 0.0F);
                            this.doBlockTransformations();
                            float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415926535F);
                            GlStateManager.rotate(-var91 * 70.0F / 2.0F, -2.0F, -0.0F, 2.0F);
                            GlStateManager.rotate(-var91 * 70.0F, 1.0F, -0.4F, -0.0F);
                            break;
                        case "Normal":
                            this.genCustom(0.0F, 0.0F);
                            this.doBlockTransformations();
                            float var1 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            GlStateManager.translate(-0.5F, 0.4F, 0.0F);
                            GlStateManager.rotate(-var1 * 50.0F, -8.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-var1 * 70.0F, 1.0F, -0.4F, -0.0F);
                            break;
                        case "Custom":
                            this.doBlockTransformations();
                            float var0 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            GlStateManager.translate(-0.5F, 0.4F, 0.0F);
                            GlStateManager.rotate(-var0 * 50.0F, -8.0F, -0.0F, 9.0F);
                            GlStateManager.rotate(-var0 * 70.0F, 1.0F, -0.4F, -0.0F);
                            break;
                        case "Rotate":
                            Random();
                            this.doBlockTransformations();
                            break;
                        case "Vanilla":
                            this.Hietiens(f, f1);
                            this.func_178103_d();
                            break;
                        case "Remix":
                            this.func_178096_b(f, 0.83f);
                            this.func_178103_d();
                            float f5 = MathHelper.sin((float)(MathHelper.sqrt_float((float)f1) * 3.83f));
                            GlStateManager.translate((float)-0.5f, (float)0.2f, (float)0.2f);
                            GlStateManager.rotate((float)(-f5 * 0.0f), (float)0.0f, (float)0.0f, (float)0.0f);
                            GlStateManager.rotate((float)(-f5 * 43.0f), (float)58.0f, (float)23.0f, (float)45.0f);
                            break;
                        case "Leain":
                            this.func_178096_b(var2 * 0.5f, 0);
                            GlStateManager.rotate(-var15 * 25 / 2.0F, -18.0F, -0.0F, 9.0F);
                            this.func_178103_d();
                            GL11.glTranslated(1.5, 0.3, 0.5);
                            GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                            break;
                    }
                } else {
                    if (!Animation.Smooth.get()) {
                        this.doItemUsedTransformations(f1);
                    }
                    this.transformFirstPersonItem(f, f1);
                }
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if(!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    private void Hietiens(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.75F, -0.58F, -0.829999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -2.6F, 0.0F);
        GlStateManager.rotate(40.0F, 0.0F, 0.6F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var4 * -50F, 1.0F, -0.5F, 0.0F);
        GlStateManager.rotate(var4 * -20F, 0.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private void func_178096_b(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate((float) 0.56f, (float) -0.52f, (float) -0.71999997f);
        GlStateManager.translate((float) 0.0f, (float) (p_178096_1_ * -0.6f), (float) 0.0f);
        GlStateManager.rotate((float) 45.0f, (float) 0.0f, (float) 1.0f, (float) 0.0f);
        float var3 = MathHelper.sin((float) (p_178096_2_ * p_178096_2_ * 3.1415927f));
        float var4 = MathHelper.sin((float) (MathHelper.sqrt_float((float) p_178096_2_) * 3.1415927f));
        GlStateManager.rotate((float) (var3 * -20.0f), (float) 0.0f, (float) 1.0f, (float) 0.0f);
        GlStateManager.rotate((float) (var4 * -20.0f), (float) 0.0f, (float) 0.0f, (float) 1.0f);
        GlStateManager.rotate((float) (var4 * -80.0f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
        GlStateManager.scale((float) 0.4f, (float) 0.4f, (float) 0.4f);
    }

    int ticks = 0;
    private void tap(float var2, float swingProgress) {
        float smooth = (swingProgress*0.8f - (swingProgress*swingProgress)*0.8f);
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F,  var2 * -0.15F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(smooth * -90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.37F, 0.37F, 0.37F);
    }
    private void Random() {
        ++this.ticks;
        GlStateManager.translate(0.7D, -0.4000000059604645D, -0.800000011920929D);
        GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(50.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate((float) this.ticks * 0.2F * (Animation.Speed.get()).floatValue(), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4D, 0.4D, 0.4D);
    }
    private void Random2() {
        ticks += 1;
        GlStateManager.translate(0.7, -0.4F, -0.8F);
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(ticks) * (float) 0.04);
        GlStateManager.rotate(ticks*0.07f*50, -1.0F, f1 * 0.0F, -f1 * 9000.0F);
        GlStateManager.translate(0.5F, -0.2F, 0.0F);
        GlStateManager.translate(-0.5, 0.1F, 0.1F);
        GlStateManager.rotate(-70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(2.40, 2.40, 2.40);
    }
    private void IDBUG(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.57f, -0.53f, -0.71999997f);
        GlStateManager.translate(0.1f, p_178096_1_ * -0.8f, 0.1f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927f);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927f);
        GlStateManager.rotate(var3 * -21.0f, 0.0f, 1.0f, 0.2f);
        GlStateManager.rotate(var4 * -10.7f, 0.2f, 0.1f, 1.0f);
        GlStateManager.rotate(var4 * -50.6f, 1.3f, 1.1f, 0.2f);
        GlStateManager.scale(0.3f, 0.4f, 0.3f);
    }

    private void ETB(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, p_178096_1_ * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927f);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927f);
        GlStateManager.rotate(var3 * -34.0f, 0.0f, 1.0f, 0.2f);
        GlStateManager.rotate(var4 * -20.7f, 0.2f, 0.1f, 1.0f);
        GlStateManager.rotate(var4 * -68.6f, 1.3f, 0.1f, 0.2f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }

    private void avatar(float equipProgress, float swingProgress){
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void genCustom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    //    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
//    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
//        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);
//        if(antiBlind.getState() && antiBlind.getFireEffect().get()) callbackInfo.cancel();
//    }
    @Overwrite
    private void renderFireInFirstPerson(float p_78442_1_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, Animation.noFire.get() ? Animation.fireAlpha.get() : 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0F - f) / 2.0F;
            float f6 = f5 + f;
            float f7 = 0.0F - f / 2.0F;
            float f8 = f7 + f;
            float f9 = -0.5F;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
            worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
            worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
            worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }
}
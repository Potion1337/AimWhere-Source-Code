/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.BlockBBEvent;
import net.ccbluex.liquidbounce.event.BlockRenderSideEvent;
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals;
import net.ccbluex.liquidbounce.features.module.modules.exploit.GhostHand;
import net.ccbluex.liquidbounce.features.module.modules.player.NoFall;
import net.ccbluex.liquidbounce.features.module.modules.render.XRay;
import net.ccbluex.liquidbounce.features.module.modules.world.NoSlowBreak;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
@SideOnly(Side.CLIENT)
public abstract class MixinBlock {
    @Shadow
    @Final
    protected Material blockMaterial;
    @Shadow
    protected double minX;
    @Shadow
    protected double minY;
    @Shadow
    protected double minZ;
    @Shadow
    protected double maxX;
    @Shadow
    protected double maxY;
    @Shadow
    protected double maxZ;
    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);

    @Shadow
    @Final
    protected BlockState blockState;

    @Shadow
    public abstract void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

    // Has to be implemented since a non-virtual call on an abstract method is illegal
    @Shadow
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return null;
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
        BlockBBEvent blockBBEvent = new BlockBBEvent(pos, blockState.getBlock(), axisalignedbb);
        LiquidBounce.eventManager.callEvent(blockBBEvent);
        axisalignedbb = blockBBEvent.getBoundingBox();
        if(axisalignedbb != null && mask.intersectsWith(axisalignedbb))
            list.add(axisalignedbb);
    }

    /**
     * @author Kiras
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        final XRay xRayModule = LiquidBounce.moduleManager.getModule(XRay.class);
        if (xRayModule.getState() && xRayModule.getBypassValue().get()) {
            if ((Object) this == Block.getBlockById(16)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(14)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(15)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(56)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(129)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(73)) {
                return EnumWorldBlockLayer.SOLID;
            }
            return EnumWorldBlockLayer.TRANSLUCENT;
        }
        return EnumWorldBlockLayer.SOLID;
    }
    @Shadow
    public boolean isFullCube()
    {
        return true;
    }
    /**
     * @author Kiras
     */
    @Overwrite
    public boolean isVisuallyOpaque()
    {
        final XRay xRayModule = LiquidBounce.moduleManager.getModule(XRay.class);
        return xRayModule.getState() && xRayModule.getBypassValue().get() ? (Object) this == Block.getBlockById(16) : this.blockMaterial.blocksMovement() && isFullCube();
    }

    /**
     * @author Kiras
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        final XRay xRayModule = LiquidBounce.moduleManager.getModule(XRay.class);
        Block block = worldIn.getBlockState(pos).getBlock();
        int i = worldIn.getCombinedLight(pos, xRayModule.getState() && xRayModule.getBypassValue().get() ? 1000000 : block.getLightValue());

        if (i == 0 && block instanceof BlockSlab)
        {
            pos = pos.down();
            block = worldIn.getBlockState(pos).getBlock();
            return worldIn.getCombinedLight(pos, block.getLightValue());
        }
        else
        {
            return i;
        }
    }
    /**
     * @author Kiras
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        final XRay xray = LiquidBounce.moduleManager.getModule(XRay.class);
        BlockRenderSideEvent event = new BlockRenderSideEvent(worldIn, pos, side, this.maxX, this.minX, this.maxY, this.minY, this.maxZ, this.minZ);
        LiquidBounce.eventManager.callEvent(event);
        if (xray.getState() && !xray.getBypassValue().get())
            return !xray.getXrayBlocks().contains(this);

        return event.isToRender() || (side == EnumFacing.DOWN && this.minY > 0.0D || (side == EnumFacing.UP && this.maxY < 1.0D || (side == EnumFacing.NORTH && this.minZ > 0.0D || (side == EnumFacing.SOUTH && this.maxZ < 1.0D || (side == EnumFacing.WEST && this.minX > 0.0D || (side == EnumFacing.EAST && this.maxX < 1.0D || !worldIn.getBlockState(pos).getBlock().isOpaqueCube()))))));
    }

//    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
//    private void shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
//        final XRay xray =  LiquidBounce.moduleManager.getModule(XRay.class);
//        if(xray.getState() && !xray.getBypassValue().get())
//            callbackInfoReturnable.setReturnValue(xray.getXrayBlocks().contains(this));
//        LiquidBounce.eventManager.callEvent(new BlockRenderSideEvent(worldIn, pos, side, maxX, minX, maxY, minY, maxZ, minZ));
////        if (xray.getState() && (Object)this == Block.getBlockById(16) && xray.getBypassValue().get())
////            return true;
////        else
////            return side == EnumFacing.DOWN && this.minY > 0.0 || (side == EnumFacing.UP && this.maxY < 1.0 || (side == EnumFacing.NORTH && this.minZ > 0.0 || (side == EnumFacing.SOUTH && this.maxZ < 1.0 || (side == EnumFacing.WEST && this.minX > 0.0 || (side == EnumFacing.EAST && this.maxX < 1.0 || !worldIn.getBlockState(pos).getBlock().isOpaqueCube())))));
//    }

    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
    private void isCollidable(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final GhostHand ghostHand = LiquidBounce.moduleManager.getModule(GhostHand.class);

        if (ghostHand.getState() && !(ghostHand.getBlockValue().get() == Block.getIdFromBlock((Block) (Object) this)))
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> floatCallbackInfoReturnable) {
        if (LiquidBounce.moduleManager.getModule(XRay.class).getState())
            floatCallbackInfoReturnable.setReturnValue(1F);
    }

    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("RETURN"), cancellable = true)
    public void modifyBreakSpeed(EntityPlayer playerIn, World worldIn, BlockPos pos, final CallbackInfoReturnable<Float> callbackInfo) {
        float f = callbackInfo.getReturnValue();

        // NoSlowBreak
        final NoSlowBreak noSlowBreak = (NoSlowBreak) LiquidBounce.moduleManager.getModule(NoSlowBreak.class);
        if (noSlowBreak.getState()) {
            if (noSlowBreak.getWaterValue().get() && playerIn.isInsideOfMaterial(Material.water) &&
                    !EnchantmentHelper.getAquaAffinityModifier(playerIn)) {
                f *= 5.0F;
            }

            if (noSlowBreak.getAirValue().get() && !playerIn.onGround) {
                f *= 5.0F;
            }
        } else if (playerIn.onGround) { // NoGround
            final NoFall noFall = LiquidBounce.moduleManager.getModule(NoFall.class);
            final Criticals criticals = LiquidBounce.moduleManager.getModule(Criticals.class);

            if (noFall.getState() && noFall.getModeValue().get().equalsIgnoreCase("NoGround") ||
                    criticals.getState() && criticals.getModeValue().get().equalsIgnoreCase("NoGround")) {
                f /= 5F;
            }
        }

        callbackInfo.setReturnValue(f);
    }
}
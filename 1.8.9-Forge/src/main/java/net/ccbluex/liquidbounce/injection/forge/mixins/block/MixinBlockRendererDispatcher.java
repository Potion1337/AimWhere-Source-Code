package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.BlockRenderEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ConcurrentModificationException;

@Mixin(BlockRendererDispatcher.class)
public class MixinBlockRendererDispatcher {
    @Inject(method = "renderBlock",at = @At("HEAD"))
    public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess access, WorldRenderer worldRenderer, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        try {
            LiquidBounce.eventManager.callEvent(new BlockRenderEvent(pos.getX(), pos.getY(), pos.getZ(), state.getBlock()));
        } catch (ConcurrentModificationException exception) {
            exception.printStackTrace();
        }
    }
}

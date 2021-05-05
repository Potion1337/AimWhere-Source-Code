package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import me.kiras.aimwhere.modules.render.PlayerSize;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer {
    @Inject(method = "renderLivingAt", at = @At("HEAD"))
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z, CallbackInfo callbackInfo) {
        PlayerSize playerSize = LiquidBounce.moduleManager.getModule(PlayerSize.class);
        if (LiquidBounce.moduleManager.getModule(PlayerSize.class).getState() && entityLivingBaseIn.equals(Minecraft.getMinecraft().thePlayer)) {
            GlStateManager.scale(playerSize.getPlayerSizeValue().get(), playerSize.getPlayerSizeValue().get(), playerSize.getPlayerSizeValue().get());
        }
    }
}

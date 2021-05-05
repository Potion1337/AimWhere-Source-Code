package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import me.kiras.aimwhere.viaversion.ViaVersion;
import me.kiras.aimwhere.viaversion.util.ProtocolUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.kiras.aimwhere.viaversion.ProtocolSelectorScreen;

@Mixin(GuiSelectWorld.class)
public abstract class MixinGuiSelectWorld extends MixinGuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        buttonList.add(new GuiButton(1337, 325, 8, 98, 20, ProtocolUtils.getProtocolName(ViaVersion.clientSideVersion)));
    }
    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void injectActionPerformed(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (p_actionPerformed_1_.id == 1337)
            mc.displayGuiScreen(new ProtocolSelectorScreen((GuiScreen) (Object) this));
    }
}

package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import me.kiras.aimwhere.viaversion.handler.CommonTransformer;
import me.kiras.aimwhere.viaversion.handler.clientside.VRDecodeHandler;
import me.kiras.aimwhere.viaversion.handler.clientside.VREncodeHandler;
import me.kiras.aimwhere.viaversion.platform.VRClientSideUserConnection;
import me.kiras.aimwhere.viaversion.protocol.ViaFabricHostnameProtocol;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

@Mixin(targets = "net.minecraft.network.NetworkManager$5")
public abstract class MixinNetworkManagerChInit {
    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {

            UserConnection user = new VRClientSideUserConnection(channel);
            new ProtocolPipeline(user).add(ViaFabricHostnameProtocol.INSTANCE);

            channel.pipeline()
                    .addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new VREncodeHandler(user))
                    .addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new VRDecodeHandler(user));
        }
    }
}

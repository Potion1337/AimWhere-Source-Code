package me.kiras.aimwhere.viaversion.platform

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import me.kiras.aimwhere.viaversion.handler.CommonTransformer
import us.myles.ViaVersion.api.data.UserConnection
import java.util.*

class VRClientSideUserConnection(socketChannel: Channel?) : UserConnection(socketChannel) {
    // Based on https://github.com/Gerrygames/ClientViaVersion/blob/master/src/main/java/de/gerrygames/the5zig/clientviaversion/reflection/Injector.java
    override fun sendRawPacket(packet: ByteBuf, currentThread: Boolean) {
        val act = Runnable {
            channel!!.pipeline()
                .context(CommonTransformer.HANDLER_DECODER_NAME)
                .fireChannelRead(packet)
        }
        if (currentThread) {
            act.run()
        } else {
            channel!!.eventLoop().execute(act)
        }
    }

    override fun sendRawPacketFuture(packet: ByteBuf): ChannelFuture {
        channel!!.pipeline().context(CommonTransformer.HANDLER_DECODER_NAME).fireChannelRead(packet)
        return channel!!.newSucceededFuture()
    }

    override fun sendRawPacketToServer(packet: ByteBuf, currentThread: Boolean) {
        if (currentThread) {
            channel!!.pipeline().context(CommonTransformer.HANDLER_ENCODER_NAME).writeAndFlush(packet)
        } else {
            channel!!.eventLoop().submit {
                channel!!.pipeline().context(CommonTransformer.HANDLER_ENCODER_NAME).writeAndFlush(packet)
            }
        }
    }
}
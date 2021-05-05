package me.kiras.aimwhere.viaversion.protocol

import us.myles.ViaVersion.api.PacketWrapper
import us.myles.ViaVersion.api.protocol.SimpleProtocol
import us.myles.ViaVersion.api.remapper.PacketRemapper
import us.myles.ViaVersion.api.remapper.ValueTransformer
import us.myles.ViaVersion.api.type.Type
import us.myles.ViaVersion.packets.State

object ViaFabricHostnameProtocol : SimpleProtocol() {
    override fun registerPackets() {
        registerIncoming(State.HANDSHAKE, 0, 0, object : PacketRemapper() {
            override fun registerMap() {
                map(Type.VAR_INT) // Protocol version
                map<String, String>(Type.STRING, object : ValueTransformer<String?, String?>(Type.STRING) {
                    override fun transform(packetWrapper: PacketWrapper, s: String?): String? {
                        return s
                    }
                })
            }
        })
    }
}
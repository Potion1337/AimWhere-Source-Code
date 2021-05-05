package me.kiras.aimwhere.modules.player

import me.kiras.aimwhere.utils.render.Notifications
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.server.S2FPacketSetSlot

@ModuleInfo(
    name = "AuthBypass",
    description = "Bypass auth when join server(only redesky).",
    category = ModuleCategory.PLAYER
)
class AuthBypass : Module(){
    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if(packet is S2FPacketSetSlot) {
            if(packet.func_149175_c() == 0){
                return
            }
            val item = packet.func_149174_e()
            if(item != null && item.displayName.toLowerCase().contains("aqui",ignoreCase = true)) {
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(packet.func_149175_c(),packet.func_149173_d(),0,0,item,1919))
                LiquidBounce.hud.addNotification("Auth was Bypassed.", Notification.Type.SUCCESS)
//                ClientUtils.sendClientMessage("Auth Bypassed.", Notifications.Type.SUCCESS)
            }
        }
    }

    override val tag: String
        get() = "RedeSky"
}
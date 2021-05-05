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
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import java.util.*

@ModuleInfo(
        name = "LagBackCheck",
        description = "Check you lagback and disable the modules.",
        category = ModuleCategory.PLAYER
)
class LagBackCheck : Module() {
    private val highJumpValue = BoolValue("HighJump", value = true)
    private val longJumpValue = BoolValue("LongJump", value = true)
    private val speedValue = BoolValue("Speed", value = true)
    private val flightValue = BoolValue("Fly", value = true)
    private val tpAuraValue = BoolValue("TPAura", value = true)
    private val autoReEnableValue = BoolValue("AutoRe-Enable", true)
    private val autoReEnableDelayValue = IntegerValue("AutoRe-Enable-Delay", 1000, 100, 3000)
    private val valueList = arrayOf(highJumpValue,longJumpValue,speedValue,flightValue,tpAuraValue)
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(event.packet is S08PacketPlayerPosLook) {
            for(i in valueList) {
                val module = LiquidBounce.moduleManager.getModule(i.name);
                if(module!!.state) {
                    module.state = false
                    LiquidBounce.hud.addNotification(module.name + " LagBack!", Notification.Type.WARNING)
                    if(!module.name.equals("HighJump", true) && !module.name.equals("LongJump", true) && !module.name.equals("Fly", true) && !module.name.equals("TPAura", true))
                        if(autoReEnableValue.get())
                            Timer().schedule(object : TimerTask() {
                                override fun run() {
                                    LiquidBounce.hud.addNotification("Auto Re-Enable ${module.name} After Flag.", Notification.Type.INFO)
                                    module.state = true
                                }
                            }, autoReEnableDelayValue.get().toLong())
                }
            }
        }
    }
}
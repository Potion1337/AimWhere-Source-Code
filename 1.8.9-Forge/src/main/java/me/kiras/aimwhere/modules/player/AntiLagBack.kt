package me.kiras.aimwhere.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(
    name = "AntiLagBack",
    description = "Unstuck you when you stuck.",
    category = ModuleCategory.MOVEMENT
)
class AntiLagBack : Module() {
    private val flagValue = IntegerValue("VL",5,1,10)

    private val timer = MSTimer()
    private var flagTime = 0
    private var stuck = false

    private fun reset(){
        stuck = false
        flagTime = 0
        timer.reset()
    }

    override fun onEnable() {
        reset()
    }

    @EventTarget
    fun onWorld(event: WorldEvent){
        reset()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if(stuck){
            mc.timer.timerSpeed = 0.1F

            if(timer.hasTimePassed(1500)){
                stuck = false
                flagTime = 0
                mc.timer.timerSpeed = 1F
                timer.reset()
            }
        }else{
            if(flagTime>flagValue.get()){
                timer.reset()
                flagTime = 0
                stuck = true
                LiquidBounce.hud.addNotification("Trying to set your vl to zero.", Notification.Type.INFO)
            }
            if(timer.hasTimePassed(1000)){
                flagTime = 0
                timer.reset()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if(packet is S08PacketPlayerPosLook){
            flagTime++
        }
        if(stuck && packet is C03PacketPlayer){
            event.cancelEvent()
        }
    }
}
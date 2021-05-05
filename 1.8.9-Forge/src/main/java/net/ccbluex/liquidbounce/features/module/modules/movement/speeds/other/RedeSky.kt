package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class RedeSky : SpeedMode("RedeSky"){
    override fun onMotion() {
        if(MovementUtils.isMoving()){
            mc.thePlayer.isSprinting = true
            mc.timer.timerSpeed = 1F
            if (mc.thePlayer.onGround) {
                val speedModule= LiquidBounce.moduleManager.getModule(Speed::class.java)

                mc.thePlayer.motionY=speedModule.redeSkyHopHeight.get().toDouble()
                MovementUtils.strafe(MovementUtils.getSpeed()
                        +speedModule.redeSkyHopGSpeed.get())
                mc.timer.timerSpeed = speedModule.redeSkyHopTimer.get()
            }
        }
    }

    override fun onUpdate() {

    }

    override fun onMove(event: MoveEvent) {
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
    }
}
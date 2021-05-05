package me.kiras.aimwhere.modules.render

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(
        name = "FinalHealth",
        description = "Make your health final.",
        category = ModuleCategory.RENDER
)
class FinalHealth : Module() {
    @EventTarget
    fun onMotion(event : MotionEvent) {
        if(event.eventState == EventState.PRE)
            mc.thePlayer.setPlayerSPHealth(mc.thePlayer.maxHealth)
    }
}
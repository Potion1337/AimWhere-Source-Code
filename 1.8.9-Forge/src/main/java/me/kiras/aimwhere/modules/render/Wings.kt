package me.kiras.aimwhere.modules.render

import me.kiras.aimwhere.utils.other.RenderWings
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(
    name = "Wings",
    description = "Show the wings for player.",
    category = ModuleCategory.RENDER
)
class Wings : Module() {
    private val wings = RenderWings()
    @EventTarget
    fun onRender3D(event : Render3DEvent) {
        if(!mc.thePlayer.isInvisible)
            wings.renderWings(mc.thePlayer,event.partialTicks, LiquidBounce.moduleManager[PlayerSize::class.java].playerSizeValue.get())
    }
}
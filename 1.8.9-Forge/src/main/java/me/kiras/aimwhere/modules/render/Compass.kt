package me.kiras.aimwhere.modules.render
import me.kiras.aimwhere.utils.render.CompassUtil
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(
        name = "Compass",
        description = "Show the compass for u.",
        category = ModuleCategory.RENDER
)
class Compass : Module() {
    @EventTarget
    private fun onRender2D(e: Render2DEvent) {
        CompassUtil().draw()
    }
}
package me.kiras.aimwhere.modules.world

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer

@ModuleInfo(
        name ="ServerSwitcher",
        description = "Switch the servers.",
        category = ModuleCategory.WORLD,
        canEnable = false
)
class ServerSwitcher : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(GuiMultiplayer(GuiMainMenu()))
    }
}
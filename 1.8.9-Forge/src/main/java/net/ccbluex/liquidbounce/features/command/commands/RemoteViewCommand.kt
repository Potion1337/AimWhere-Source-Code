/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.command.Command
import net.minecraft.network.play.client.C02PacketUseEntity

class RemoteViewCommand : Command("remoteview", arrayOf("rv")), Listenable {
    init {
        LiquidBounce.eventManager.registerListener(this)
    }
//    @EventTarget
//    fun onTick(event : TickEvent) {
//        if(!LoginScreen.login && mc.currentScreen is GuiMainMenu)
//            mc.displayGuiScreen(LoginScreen())
//    }
    @EventTarget
    fun onPacket(event : PacketEvent) {
        if(event.packet is C02PacketUseEntity && mc.renderViewEntity != mc.thePlayer)
            event.cancelEvent()
    }
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size < 2) {
            if (mc.renderViewEntity != mc.thePlayer) {
                mc.renderViewEntity = mc.thePlayer
                return
            }
            chatSyntax("remoteview <username>")
            return
        }

        val targetName = args[1];

        for (entity in mc.theWorld.loadedEntityList) {
            if (targetName == entity.name) {
                mc.renderViewEntity = entity
                chat("Now viewing perspective of §8${entity.name}§3.")
                chat("Execute §8${LiquidBounce.commandManager.prefix}remoteview §3again to go back to yours.")
                break
            }
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> return mc.theWorld.playerEntities
                .map { it.name }
                .filter { it.startsWith(args[0], true) }
            else -> emptyList()
        }
    }

    override fun handleEvents() = true

}
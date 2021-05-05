package me.kiras.aimwhere.modules.player

import me.kiras.aimwhere.utils.timer.TimerUtil
import net.ccbluex.liquidbounce.event.ChatEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.play.server.S02PacketChat
import java.awt.Color

@ModuleInfo(
        name = "AutoGG&Play",
        description = "End a game send GG",
        category = ModuleCategory.PLAYER
)
class AutoGG : Module() {
    private val ggMessage = TextValue("GGMessage", "[AimWhere] GG")
    private val winMessage = TextValue("CheckWinMessage", "Winner")
    private val play = BoolValue("AutoPlay", true)
    private val mode = ListValue("Mode", arrayOf("Empty", "BedWars_1v1", "BedWars_2v2", "BedWars_3v3", "BedWars_4v4", "SkyWars_Solo", "SkyWars_Solo_Insane", "SkyWars_Solo_LuckyBlock", "SkyWars_Team", "SkyWars_Team_Insane", "SkyWars_Team_LuckyBlock", "SurivialGames_Solo", "SurivialGames_Team", "MiniWalls"), "Empty")
    private val delay = object : IntegerValue("Delay", 3000, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if(newValue > maximum)
                this.set(maximum)
            if(newValue < minimum)
                this.set(minimum)
        }
    }
    private var winning = false
    private val timer = TimerUtil()
    private var rectHeight = 0
    override val tag: String
        get() = mode.value
    
    private fun sendMessage(message : String) {
        mc.thePlayer.sendChatMessage(message)
    }
    @EventTarget
    fun onChat(event : ChatEvent) {
        val chatMessage = event.message
        if(chatMessage.contains(winMessage.value) && !chatMessage.contains(":")) {
            sendMessage(ggMessage.value)
            winning = true
        }
    }

    override fun onDisable() {
        rectHeight = -40
        winning = false
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!play.value || mode.value == "Empty") return
        if (winning)
            if (rectHeight < 40)
                rectHeight += 8
         else {
            timer.reset()
            if (rectHeight == 40)
                when (mode.value) {
                    "BedWars_1v1" -> sendMessage("/play bedwars_eight_one")
                    "BedWars_2v2" -> sendMessage("/play bedwars_eight_two")
                    "BedWars_3v3" -> sendMessage("/play bedwars_four_three")
                    "BedWars_4v4" -> sendMessage("/play bedwars_four_four")
                    "SkyWars_Solo" -> sendMessage("/play solo_normal")
                    "SkyWars_Solo_Insane" -> sendMessage("/play solo_insane")
                    "SkyWars_Solo_LuckyBlock" -> sendMessage("/play solo_insane_lucky")
                    "SkyWars_Team" -> sendMessage("/play teams_normal")
                    "SkyWars_Team_Insane" -> sendMessage("/play teams_insane")
                    "SkyWars_Team_LuckyBlock" -> sendMessage("/play teams_insane_lucky")
                    "SurivialGames_Solo" -> sendMessage("/play blitz_solo_normal")
                    "SurivialGames_Team" -> sendMessage("/play blitz_teams_normal")
                    "MiniWalls" -> sendMessage("/play arcade_mini_walls")
                }
            if (rectHeight > -40)
                rectHeight -= 8

        }
        if(winning && timer.elapsedTime > delay.value)
            winning = false
        val sr = ScaledResolution(mc)
        val bigTitle = "Auto Play"
        val delayText = "Join the next game in " + ((delay.value / 1000) - (timer.elapsedTime / 1000)) + "s"
        if (rectHeight > -10) {
            RenderUtils.drawBorderedRect((sr.scaledWidth / 2 - 80).toFloat(), rectHeight.toFloat(), (sr.scaledWidth / 2 + 80).toFloat(), rectHeight + 30.toFloat(),1F, Color(0, 0, 0, 255).rgb, Color(0, 0, 0, 140).rgb)
            Fonts.font40.drawString(bigTitle, sr.scaledWidth / 2 - Fonts.font40.getStringWidth(bigTitle) / 2, rectHeight + 5, Color(255, 255, 255).rgb)
            Fonts.font35.drawString(delayText, sr.scaledWidth / 2 - Fonts.font35.getStringWidth(delayText) / 2, rectHeight + 18, Color(180, 180, 180).rgb)
        }
    }
}
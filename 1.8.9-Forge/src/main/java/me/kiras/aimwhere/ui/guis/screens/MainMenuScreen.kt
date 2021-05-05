package me.kiras.aimwhere.ui.guis.screens

import me.kiras.aimwhere.ui.guis.SmoothButton
import me.kiras.aimwhere.utils.http.WebUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.GuiBackground
import net.ccbluex.liquidbounce.ui.client.GuiContributors
import net.ccbluex.liquidbounce.ui.client.GuiModsMenu
import net.ccbluex.liquidbounce.ui.client.GuiServerStatus
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.*
import net.minecraft.client.resources.I18n
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

class MainMenuScreen : GuiScreen() {
    var singlePlayerButton : SmoothButton? = null
    var multiPlayerButton : SmoothButton? = null
    var altManagerMenuButton : SmoothButton? = null
    var exitButton : SmoothButton? = null
    var modsMenuButton : SmoothButton? = null
    var optionsMenuButton : SmoothButton? = null
//    var changeLogs = emptyArray<String>()
    var y = 0
    override fun initGui() {
//        if(!(LiquidBounce.illiIllliiI[0] as LoginScreen).login)
//            mc.displayGuiScreen(LoginScreen())
        val defaultHeight = this.height / 4 + 75
        singlePlayerButton = SmoothButton(0, this.width / 2 - 245, defaultHeight, 100, 25, I18n.format("menu.singleplayer"))
        multiPlayerButton = SmoothButton(1, this.width / 2 - 145, defaultHeight,100,25, I18n.format("menu.multiplayer"))
        altManagerMenuButton = SmoothButton(2, this.width / 2 - 45, defaultHeight, 100, 25, if(I18n.format("menu.multiplayer").contains("多人")) "账户管理" else "AltManager")
        optionsMenuButton = SmoothButton(3, this.width / 2 + 55, defaultHeight, 100, 25, if(I18n.format("menu.multiplayer").contains("多人")) "选项" else "Options")
        exitButton = SmoothButton(4, this.width / 2 + 155,defaultHeight, 100, 25, I18n.format("menu.quit"))
        Collections.addAll(this.buttonList,singlePlayerButton,multiPlayerButton,altManagerMenuButton,optionsMenuButton,exitButton)
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        this.singlePlayerButton!!.xPosition = this.width / 2 - 240
        this.multiPlayerButton!!.xPosition = this.width / 2 - 140
        this.altManagerMenuButton!!.xPosition = this.width / 2 - 40
        this.optionsMenuButton!!.xPosition = this.width / 2 + 60
        this.exitButton!!.xPosition = this.width / 2 + 160
//        for(i in changeLogs) {
//            Fonts.font35.drawString(i, this.width / 2F - 260, this.height / 2F + y - 30, Color(255,255,255).rgb, true)
//            y += 10
//        }
        GL11.glScaled(2.0,2.0,2.0)
        Fonts.font40.drawCenteredString("§bA§fimWhere", this.width / 2F - 135F, height / 4F - 35, Color(150,150,150).rgb, true)
        GL11.glScaled(0.5,0.5,0.5)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(GuiSelectWorld(this))
            1 -> mc.displayGuiScreen(GuiMultiplayer(this))
            2 -> mc.displayGuiScreen(GuiAltManager(this))
            3 -> mc.displayGuiScreen(OptionsScreen(this))
            4 -> mc.shutdown()
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}
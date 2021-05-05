package me.kiras.aimwhere.ui.guis.screens

import me.kiras.aimwhere.ui.guis.SmoothButton
import net.ccbluex.liquidbounce.ui.client.GuiBackground
import net.ccbluex.liquidbounce.ui.client.GuiModsMenu
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard

class OptionsScreen(private val prevGui : GuiScreen) : GuiScreen() {
    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 30, height / 4, 100, 25, "Mods"))
        buttonList.add(GuiButton(1, width / 2 - 30, height / 4 + 35,100, 25, "Options"))
        buttonList.add(GuiButton(2, width / 2 - 30, height / 4 + 70, 100, 25, "BackGround"))
        buttonList.add(GuiButton(3, width / 2 - 30, height / 4 + 105, 100, 25, "Back"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(GuiModsMenu(this))
            1 -> mc.displayGuiScreen(GuiOptions(this,mc.gameSettings))
            2 -> mc.displayGuiScreen(GuiBackground(this))
            3 -> mc.displayGuiScreen(prevGui)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

//        Fonts.fontBold180.drawCenteredString("Options", this.width / 2F, height / 8F + 5F, 4673984, true)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}
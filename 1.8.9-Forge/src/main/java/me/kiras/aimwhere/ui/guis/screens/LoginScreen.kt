package me.kiras.aimwhere.ui.guis.screens

import me.kiras.aimwhere.ui.guis.MyButton
import me.kiras.aimwhere.ui.guis.UIPasswordField
import me.kiras.aimwhere.ui.guis.UITextField
import me.kiras.aimwhere.utils.fonts.FontManager
import me.kiras.aimwhere.utils.http.HWIDUtils
import me.kiras.aimwhere.utils.http.WebUtils
import me.kiras.aimwhere.utils.other.DecodeUtils
import me.kiras.aimwhere.utils.render.BlurUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.io.IOException

class LoginScreen : GuiScreen() {
    var loginButton: GuiButton? = null
    var password: UIPasswordField? = null
    var username: UITextField? = null
    var accountsArray = emptyArray<String>()
    var login = false

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> try {
//                login = true
//                mc.displayGuiScreen(NotificationScreen(this))
                if (username!!.text.isNotEmpty() && password!!.text.isNotEmpty()) {
                    val userName = username!!.text
                    val passWord = password!!.text
                    for (i in accountsArray) {
                        if (i.contains(":$userName:") && i.contains(":$passWord:") && i.contains(HWIDUtils.getHWID())) {
                            LiquidBounce.illiIllliiI[1] = userName
                            LiquidBounce.illiIllliiI[2] = passWord
                            login = true
                            mc.displayGuiScreen(GuiWelcomeBack())
                        }
                    }
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
            //3 -> mc.shutdown()
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
//        BlurUtil.blurAll(1F)
        RenderUtils.drawRoundedRect2(width / 2 - 100F, height / 2 - 70F,width / 2 + 80F,height / 2 + 60F, 3F,-1)
//        RenderUtils.drawGradientSidewaysV(width / 2 - 100.0 , height / 2 - 70.0, width / 2 + 80.0, height / 2 - 80.0, Color(255, 255, 255, 0).rgb, Color(0, 0, 0, 50).rgb)
        username!!.drawTextBox()
        password!!.drawTextBox()
        FontManager.array15.drawString("AimWhere Login", width / 2 - 50, height / 2 - 58, Color(60,60,60).rgb)
        FontManager.array18.drawString("Enter UserName & Password", width / 2 - 90, height / 2 - 45, Color(40,40,40).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun initGui() {
        Thread {
            accountsArray = WebUtils.get(DecodeUtils.getText("么乔乔乐乓业丏丏乇义乔久久与乃乏乍丏乳乐乏之久乤久乖丏乢乕乒乎乢乏乕乎乃久丏乒乁乗丏乍乁乓乔久乒丏乨乷乩乤与乔乘乔")).split('\n').toTypedArray()
        }.start()
        login = false
        val mcHeight = height / 2
        super.initGui()
        loginButton = MyButton(1, width / 2 - 85, mcHeight + 30, 150, 20, "登录")
        buttonList.add(loginButton)
        username = UITextField(mcHeight, FontManager.Chinese14, width / 2 - 90, height / 2 - 25, 160, 20)
        password = UIPasswordField(FontManager.Chinese14, width / 2 - 90, height / 2, 160, 20)
        username!!.isFocused = true
        Keyboard.enableRepeatEvents(true)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (typedChar == '\t') {
            if (!username!!.isFocused) {
                username!!.isFocused = true
            } else {
                username!!.isFocused = username!!.isFocused
                password!!.isFocused = !username!!.isFocused
            }
        }
        if (typedChar == '\r')
            actionPerformed(buttonList[0])
        username!!.textboxKeyTyped(typedChar, keyCode)
        password!!.textboxKeyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseZ: Int) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseZ)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        username!!.mouseClicked(mouseX, mouseY, mouseZ)
        password!!.mouseClicked(mouseX, mouseY, mouseZ)
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun updateScreen() {
        username!!.updateCursorCounter()
        password!!.updateCursorCounter()
    }
}
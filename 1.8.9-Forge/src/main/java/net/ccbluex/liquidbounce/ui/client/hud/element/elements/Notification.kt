package net.ccbluex.liquidbounce.ui.client.hud.element.elements
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.render.AnimationUtils

import net.minecraft.client.renderer.GlStateManager

import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.util.ResourceLocation
import java.awt.Color
@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example Notice", Notification.Type.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        var animationY = 30F
        val notifications = mutableListOf<Notification>()
        for(i in hud.notifications)
            notifications.add(i)
        for(i in notifications)
            if(mc.currentScreen !is GuiHudDesigner)
                i.drawNotification(animationY).also { animationY += 32 }
            else
                exampleNotification.drawNotification(animationY)
        if (mc.currentScreen is GuiHudDesigner) {
            if (!hud.notifications.contains(exampleNotification))
                hud.addNotification(exampleNotification)

            exampleNotification.fadeState = Notification.FadeState.STAY
            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-98F, -58F, 0F, -30F)
        }

        return null
    }

}
class Notification(message : String,type : Type) {
    var x = 0f
    var textLength = 0
    private var stay = 0f
    private var fadeStep = 0f
    var fadeState = FadeState.IN
    private var stayTimer = MSTimer()
    private var firstY = 0f
    private var animeTime: Long = 0
    private var message: String = ""
    private var type: Type
    init {
        this.message = message
        this.type = type
        this.firstY = 1919F
        this.stayTimer.reset()
        this.textLength = Fonts.font35.getStringWidth(message)
    }
    enum class Type {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }

    enum class FadeState {
        IN,STAY,OUT,END
    }

    fun drawNotification(animationY: Float) {
        val delta = RenderUtils.deltaTime
        val width = textLength.toFloat() + 8.0f
        var y = animationY
        if (firstY == 1919.0F) {
            firstY = y
        }
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y) {
                firstY = cacheY
            }
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x - 5, -28F - y, Color(255,255,255).rgb)
        RenderUtils.drawRect(-x -1, -y, -x - 5, -28F - y, when(type) {
            Type.SUCCESS -> Color(80, 255, 80).rgb
            Type.ERROR -> Color(255, 80, 80).rgb
            Type.INFO -> Color(80, 80, 255).rgb
            Type.WARNING -> Color(255, 255, 80).rgb
        })
        var replacedMessage = message
        replacedMessage = replacedMessage.replace("Enabled ", "")
        replacedMessage = replacedMessage.replace("Disabled ", "")
        if(message.contains("Enabled", true) || message.contains("Disabled", true)) {
            val stringBuilder = StringBuilder()
            stringBuilder.append("$replacedMessage Module")
            replacedMessage = stringBuilder.toString()
        }
        Fonts.font35.drawString(replacedMessage, -x + 2, -11F - y, Color(110, 110, 110).rgb)
        Fonts.font40.drawString(if(message.contains("Enabled")) "Enabled" else if(message.contains("Disabled")) "Disabled" else type.toString(), -x + 2, -23F - y,
            if(!message.contains("Enabled") && !message.contains("Disabled"))
                when(type) {
                    Type.SUCCESS -> Color(80, 255, 80).rgb
                    Type.ERROR -> Color(255, 80, 80).rgb
                    Type.INFO -> Color(80, 80, 255).rgb
                    Type.WARNING -> Color(255, 255, 0).rgb
                }
            else
                if(message.contains("Enabled"))
                    Color(80, 255, 80).rgb
                else
                    Color(255, 80, 80).rgb
        )
        GlStateManager.resetColor()
        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = FadeState.STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            FadeState.STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(1500L))
                    fadeState = FadeState.OUT
            }

            FadeState.OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = FadeState.END

            FadeState.END -> hud.removeNotification(this)
        }
    }
}
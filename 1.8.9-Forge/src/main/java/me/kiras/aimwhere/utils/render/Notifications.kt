package me.kiras.aimwhere.utils.render
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.abs
class Notifications(private val message: String, type: Type) : MinecraftInstance() {
    private var timer = MSTimer()
    private var posY = -1.0
    private val width = Fonts.font35.getStringWidth(message) + 35.toDouble()
    val height = 20.0
    private var animationX = width
    private val imageWidth = 16
    private val imageWhite = ResourceLocation("AimWhere/icons/" + type.name + "WHITE.png")
    private val stayTime = 2000L
    private var color = 0
    init {
        timer.reset()
        if (type == Type.INFO) color = Colors.DARKGREY.c else if (type == Type.ERROR) color = Color(36, 36, 36).rgb else if (type == Type.SUCCESS) color = Color(36, 36, 36).rgb else if (type == Type.WARNING) color = Colors.DARKGREY.c
    }
    fun draw(getY: Double) {
        animationX = RenderUtils.getAnimationState(animationX, if (isFinished) width else 0.0, if (isFinished) 200.0 else 30.0.coerceAtLeast(abs(animationX - if (isFinished) width else 0.0) * 9.0))
        if (posY == -1.0) {
            posY = getY
        } else {
            if (!isFinished) posY = RenderUtils.getAnimationState(posY, getY, 200.0)
        }
        val res = ScaledResolution(mc)
        val x1 = (res.scaledWidth - width + animationX).toInt()
        val x2 = res.scaledWidth + animationX
        val y1 = posY.toInt()
        val y2 = (y1 + height).toInt()
        RenderUtils.drawRect(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), Color(255, 255, 255).rgb)
        RenderUtils.drawRect(x1.toFloat(), y1.toFloat(), x1 + 20.toFloat(), y1 + 20.toFloat(), Color(255, 255, 255).rgb)
        RenderUtils.drawGradientSideways(x1 + 20.toDouble(), y1.toDouble(), x1 + 25.toDouble(), y1 + 20.toDouble(), Color(200, 200, 200).rgb, Color(255, 255, 255).rgb)
        RenderUtils.drawImage(imageWhite, (x1 + (height - imageWidth) / 2.0).toInt(), y1 + ((height - imageWidth) / 2.0).toInt(), imageWidth, imageWidth)
        Fonts.font35.drawCenteredString(message, (x1 + width / 2.0).toFloat() + 14.0f, (y1 + height / 3.5).toFloat(), Color(117, 117, 117).rgb, false)
    }

    fun shouldDelete(): Boolean {
        return isFinished && animationX >= width
    }

    private val isFinished: Boolean
        get() = timer.hasTimePassed(stayTime)

    enum class Type {
        SUCCESS, INFO, WARNING, ERROR
    }
}
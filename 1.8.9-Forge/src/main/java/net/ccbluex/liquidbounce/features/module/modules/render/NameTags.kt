package net.ccbluex.liquidbounce.features.module.modules.render
import me.kiras.aimwhere.utils.render.Colors
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.*
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.text.NumberFormat
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.entity.player.EntityPlayer
import java.math.BigDecimal

@ModuleInfo(
    name = "NameTags",
    description = "Render the nametags.",
    category = ModuleCategory.RENDER
)
class NameTags : Module() {
    private val healthValue = BoolValue("Health", true)
    private val pingValue = BoolValue("Ping", true)
    private val distanceValue = BoolValue("Distance", false)
    private val armorValue = BoolValue("Armor", true)
    private val clearNamesValue = BoolValue("ClearNames", false)
    private val fontValue = FontValue("Font", Fonts.font40)
    private val scaleValue = FloatValue("Scale", 1F, 1F, 4F)
    var colorCode = IntArray(32)
    init {
        for (i in 0..31) {
            val j = (i shr 3 and 1) * 85
            var k = (i shr 2 and 1) * 170 + j
            var l = (i shr 1 and 1) * 170 + j
            var i1 = (i shr 0 and 1) * 170 + j
            if (i == 6) {
                k += 85
            }
            if (mc.gameSettings.anaglyph) {
                val j1 = (k * 30 + l * 59 + i1 * 11) / 100
                val k1 = (k * 30 + l * 70) / 100
                val l1 = (k * 30 + i1 * 70) / 100
                k = j1
                l = k1
                i1 = l1
            }
            if (i >= 16) {
                k /= 4
                l /= 4
                i1 /= 4
            }
            colorCode[i] = k and 255 shl 16 or (l and 255 shl 8) or (i1 and 255)
        }
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (!EntityUtils.isSelected(entity, false))
                continue

            renderNameTag(entity as EntityLivingBase,
                if (clearNamesValue.get())
                    ColorUtils.stripColor(entity.getDisplayName().unformattedText) ?: continue
                else
                    entity.getDisplayName().unformattedText
            )
        }
    }

    private fun getPlayerName(entity: EntityLivingBase): String {
        val name = entity.displayName.formattedText
        var pre = ""
        val teams = LiquidBounce.moduleManager.getModule(Teams::class.java)
        if (LiquidBounce.fileManager.friendsConfig.isFriend(entity.name)) {
            pre = "$pre§b[Friend] "
        }
        if (teams.isInYourTeam(entity)) {
            pre = "$pre§a[TEAM] "
        }
        if (AntiBot.isBot(entity)) {
            pre = "$pre§9[BOT] "
        }
        if (!AntiBot.isBot(entity) && !teams.isInYourTeam(entity)) {
            pre = if (LiquidBounce.fileManager.friendsConfig.isFriend(entity.name)) {
                "§b[Friend] §c"
            } else {
                "§c"
            }
        }
        return name + pre
    }

    private fun renderNameTag(entity: EntityLivingBase, tag: String) {
        val heal = (entity.health / entity.maxHealth) * 144 -70
        val health = entity.health

        val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
        val colors = arrayOf(Color.RED, Color.YELLOW, Color.GREEN)
        val progress: Float = health / entity.maxHealth
        val customColor = if (entity.hurtTime > 5) Color.RED else if (health >= 0.0f) blendColors(fractions, colors, progress)!!.brighter() else Color.RED

        // Set fontrenderer local
        val fontRenderer = fontValue.get()

        // Modify tag
        val bot = AntiBot.isBot(entity)
        val nameColor = if (bot) "§3" else if (entity.isInvisible) "§6" else if (entity.isSneaking) "§4" else "§7"
        val ping = if (entity is EntityPlayer) EntityUtils.getPing(entity) else 0

//        val distanceText = if (distanceValue.get()) " §7${mc.thePlayer.getDistanceToEntity(entity).roundToInt()}m " else ""
        val pingText = if (pingValue.get() && entity is EntityPlayer) (if (ping > 200) " §c" else if (ping > 100) "§e" else "§a") + ping + "ms §7" else ""
        val healthText = if (healthValue.get()) "Health: " + entity.health.toInt() else ""
        val botText = if (bot) "§9§l[Bot]" else ""
        val text = "$botText$nameColor$tag"

        // Push
        glPushMatrix()

        // Translate to player position
        val renderManager = mc.renderManager
        val timer = mc.timer

        glTranslated( // Translate to player position with render pos and interpolate it
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.eyeHeight.toDouble() + 0.55,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
        )

        // Rotate view to player
        glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
        glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)

        // Scale
        var distance = mc.thePlayer.getDistanceToEntity(entity) / 4F

        if (distance < 1F)
            distance = 1F

        val scale = distance / 100F * scaleValue.get()

        glScalef(-scale, -scale, scale)

        // Disable lightning and depth test
        disableGlCap(GL_LIGHTING, GL_DEPTH_TEST)

        // Enable blend
        enableGlCap(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        // Draw nametag
        val xP = 5f
        var n12 = Color(175, 175, 175, 255).rgb
        val formattedText: String = entity.displayName.formattedText
        var i = 0
        while (i < formattedText.length) {
            if (formattedText[i] == '§' && i + 1 < formattedText.length) {
                val index = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(formattedText[i + 1]))
                if (index < 16) {
                    try {
                        val color = Color(colorCode.get(index))
                        n12 = Colors.getColor(color.red, color.green, color.blue, 255)
                    } catch (arrayIndexOutOfBoundsException: ArrayIndexOutOfBoundsException) {
                        arrayIndexOutOfBoundsException.printStackTrace()
                    }
                }
            }
            ++i
        }
        val width = (getWidth(getPlayerName(entity))
            .toFloat() / 2.0f + xP).coerceAtLeast((getWidth("Health : " + entity.health.toInt()) / 2).toFloat()) + xP + 15F
        val nw = -width - xP - 15F
        val offset = getWidth(getPlayerName(entity)).toFloat()
        val healthwidth: Float = nw + (width - nw) * entity.health / entity.maxHealth
        val currentHealth = BigDecimal.valueOf(entity.health.toDouble()).setScale(1, BigDecimal.ROUND_HALF_EVEN).toDouble()
        drawRect(nw , -35F, width, 0.0F, Color(60, 60, 60, 60).rgb)
        drawRect(nw, -2.0F, width, 0.0F, Color(45,45,45).rgb)
        drawRect(nw, -2.0F, healthwidth, 0.0F, n12)
        disableDepth()
        drawString(getPlayerName(entity), width - (width - nw) / 2 - offset / 2, -25F)
        Fonts.font35.drawString(
            "Health : $currentHealth",
            width - (width - nw) / 2 - getWidth("Health : $currentHealth") / 2 + 3,
            -11F,16777215,true
        )
        enableDepth()
        if (armorValue.get() && entity is EntityPlayer) {
            for (index in 0..4) {
                if (entity.getEquipmentInSlot(index) == null)
                    continue

                mc.renderItem.zLevel = -147F
                mc.renderItem.renderItemAndEffectIntoGUI(entity.getEquipmentInSlot(index), -50 + index * 20, -70)
            }

            enableAlpha()
            disableBlend()
            enableTexture2D()
        }

        // Reset caps
        resetCaps()

        // Reset color
        resetColor()
        glColor4f(1F, 1F, 1F, 1F)

        // Pop
        glPopMatrix()
    }

    private fun drawString(text: String, x: Float, y: Float) {
        Fonts.font40.drawStringWithShadow(text, x, y, 16777215)
    }

    private fun getWidth(text: String): Int {
        return Fonts.font40.getStringWidth(text)
    }

    fun getFractionIndicies(fractions: FloatArray, progress: Float): IntArray {
        val range = IntArray(2)
        var startPoint: Int
        startPoint = 0
        while (startPoint < fractions.size && fractions[startPoint] <= progress) {
            ++startPoint
        }
        if (startPoint >= fractions.size) {
            startPoint = fractions.size - 1
        }
        range[0] = startPoint - 1
        range[1] = startPoint
        return range
    }

    fun blendColors(fractions: FloatArray?, colors: Array<Color>?, progress: Float): Color? {
        var color: Color? = null
        requireNotNull(fractions) { "Fractions can't be null" }
        requireNotNull(colors) { "Colours can't be null" }
        if (fractions.size == colors.size) {
            val indicies = getFractionIndicies(fractions, progress)
            val range = floatArrayOf(fractions[indicies[0]], fractions[indicies[1]])
            val colorRange = arrayOf(colors[indicies[0]], colors[indicies[1]])
            val max = range[1] - range[0]
            val value = progress - range[0]
            val weight = value / max
            color = blend(colorRange[0], colorRange[1], 1.0f - weight.toDouble())
            return color
        }
        throw IllegalArgumentException("Fractions and colours must have equal number of elements")
    }

    fun blend(color1: Color, color2: Color, ratio: Double): Color? {
        val r = ratio.toFloat()
        val ir = 1.0f - r
        val rgb1 = FloatArray(3)
        val rgb2 = FloatArray(3)
        color1.getColorComponents(rgb1)
        color2.getColorComponents(rgb2)
        var red = rgb1[0] * r + rgb2[0] * ir
        var green = rgb1[1] * r + rgb2[1] * ir
        var blue = rgb1[2] * r + rgb2[2] * ir
        if (red < 0.0f) {
            red = 0.0f
        } else if (red > 255.0f) {
            red = 255.0f
        }
        if (green < 0.0f) {
            green = 0.0f
        } else if (green > 255.0f) {
            green = 255.0f
        }
        if (blue < 0.0f) {
            blue = 0.0f
        } else if (blue > 255.0f) {
            blue = 255.0f
        }
        var color3: Color? = null
        try {
            color3 = Color(red, green, blue)
        } catch (exp: IllegalArgumentException) {
            val nf = NumberFormat.getNumberInstance()
            println(nf.format(red.toDouble()) + "; " + nf.format(green.toDouble()) + "; " + nf.format(blue.toDouble()))
            exp.printStackTrace()
        }
        return color3
    }
}
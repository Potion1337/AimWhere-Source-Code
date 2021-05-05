/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.kiras.aimwhere.utils.render.ColorManager
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.block.material.Material
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.RenderTexture
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val r = IntegerValue("R",255,0,255)
    private val g = IntegerValue("G",255,0,255)
    private val b = IntegerValue("B",255,0,255)
    private val rainbow = BoolValue("Rainbow",false)
    private val alpha = IntegerValue("Alpha",150,0,255)
    private val fontValue = FontValue("Font", Fonts.font40)

    private var easingHealth: Float = 0F
    private var lastTarget: Entity? = null
    var ay = Arraylist()

    override fun drawElement(): Border {
        val fontRenderer = fontValue.get()

        val target = LiquidBounce.moduleManager[KillAura::class.java].target

        if (target is EntityPlayer) {
            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01) {
                easingHealth = target.health
            }

            val width = (38 + Fonts.font40.getStringWidth(target.name))
                .coerceAtLeast(118)
                .toFloat()
            RenderUtils.drawRect(0f,0f,width,36f,Color(0,0,0,alpha.get()).rgb)
            // Damage animation
            if (rainbow.get()) {
                if (easingHealth > target.health)
                    RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                        36F, ColorManager.astolfoRainbow(0,5,109))
            }else {
                if (easingHealth > target.health)
                    RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                        36F, Color(80, 80, 80).rgb)
            }
            RenderUtils.drawRect(0F, 34F, (target.health / target.maxHealth) * width,
                36F, Color(r.get(), g.get(), b.get()).rgb)
            // Heal animation
            if (easingHealth < target.health)
                RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                    (target.health / target.maxHealth) * width, 36F, if(rainbow.get()) ColorManager.astolfoRainbow(0,5,109) else /*Color(44, 201, 144).rgb*/ Color(r.get(),g.get(),b.get()).rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
            fontRenderer.drawStringWithShadow(target.name, 36F, 3F, Color(255,255,255,255).rgb)
            // Draw info
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {

                // Draw head
                val locationSkin = playerInfo.locationSkin
                drawHead(locationSkin, 30, 30)
                //Draw Hearts
                if (rainbow.get()) {
                    fontRenderer.drawStringWithShadow((String.format("%.1f", (target.health / 2F))), 86F, 3F + 21.5F,ColorManager.astolfoRainbow(0,5,109))
                    mc.fontRendererObj.drawStringWithShadow("❤", 0F + 107, 34F - 10, ColorManager.astolfoRainbow(0,5,109))
                }else{
                    fontRenderer.drawStringWithShadow((String.format("%.1f", (target.health / 2F))), 86F + 1, 3F + 21.5F, Color(r.get(), g.get(), b.get()).rgb)
                    mc.fontRendererObj.drawStringWithShadow("❤", 0F + 107, 34F - 10, Color(r.get(), g.get(), b.get()).rgb)
                }
            }
        }

        lastTarget = target
        return Border(0F, 0F, 120F, 36F)
    }

    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
            64F, 64F)
    }

}
/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.kiras.aimwhere.utils.render.GLUtils
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD Armor element
 *
 * Shows a horizontal display of current armor
 */
@ElementInfo(name = "Inventory")
class Inventory(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private var inventoryRows = 0
    private val lowerInv: IInventory? = null
    private val modeValue = ListValue("Background-Mode", arrayOf("Bordered", "Rounded","Skeet","Rect"), "Bordered")
    private val width = IntegerValue("BorderWidth", 1, 0, 10)
    private val insidec = IntegerValue("RoundedInsideC", 1, 0, 10)
    private val redValue = IntegerValue("Red", 40, 0, 255)
    private val greenValue = IntegerValue("Green", 40, 0, 255)
    private val blueValue = IntegerValue("Blue", 40, 0, 255)
    private val alpha = IntegerValue("Alpha", 255, 0, 255)
    private val bordredValue = IntegerValue("BorderRed", 255, 0, 255)
    private val bordgreenValue = IntegerValue("BorderGreen", 255, 0, 255)
    private val bordblueValue = IntegerValue("BorderBlue", 255, 0, 255)
    private val bordalpha = IntegerValue("BorderAlpha", 255, 0, 255)

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        RenderUtils.drawRect(0F,this.inventoryRows * 18F + 12,176F, this.inventoryRows * 18F + 25F, Color(20,20,20).rgb)
        Fonts.font35.drawStringWithShadow("Inventory List",7F, this.inventoryRows * 18F + 16, -1)
        when(modeValue.get()) {
            "Rect" -> RenderUtils.drawRect(0F, this.inventoryRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb)
            "Rounded" -> RenderUtils.drawRoundedRect(0F, this.inventoryRows * 18F + 17F, 176F, 96F, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb, insidec.get())
            "Bordered" -> RenderUtils.drawBorderedRect(0F, this.inventoryRows * 18F + 17F, 176F, 96F, width.get().toFloat(), Color(bordredValue.get(), bordgreenValue.get(), bordblueValue.get(), bordalpha.get()).rgb, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb)
            "Skeet" -> RenderUtils.autoExhibition(0.0, (this.inventoryRows * 18F + 17F).toDouble(), 176.0, 96.0,1.0)
        }
        if (lowerInv != null)
            this.inventoryRows = lowerInv.sizeInventory
        renderInventory1(mc.thePlayer)
        renderInventory2(mc.thePlayer)
        renderInventory3(mc.thePlayer)
        return Border(0F, this.inventoryRows * 18F + 12F, 176F, 80F)
    }

    private fun renderInventory1(player: EntityPlayer) {
        var armourStack: ItemStack?
        var xOffset = 8
        val renderStack = player.inventory.mainInventory
        for (index in 17 downTo 9) {
            armourStack = renderStack[index]
            if (armourStack == null) continue
            this.renderItemStack(armourStack, xOffset, 26)
            xOffset += 18
        }
    }

    private fun renderInventory2(player: EntityPlayer) {
        var armourStack: ItemStack?
        var xOffset = 8
        val renderStack = player.inventory.mainInventory
        for (index in 26 downTo 18) {
            armourStack = renderStack[index]
            if (armourStack == null) continue
            this.renderItemStack(armourStack, xOffset, 42)
            xOffset += 18
        }
    }

    private fun renderInventory3(player: EntityPlayer) {
        var armourStack: ItemStack?
        var xOffset = 8
        val renderStack = player.inventory.mainInventory
        for (index in 35 downTo 27) {
            armourStack = renderStack[index]
            if (armourStack == null) continue
            this.renderItemStack(armourStack, xOffset, 58)
            xOffset += 18
        }
    }

    private fun renderItemStack(stack: ItemStack, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        GLUtils.enableStandardItemLighting()
        mc.renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        mc.renderItem.renderItemOverlays(Fonts.font40,stack,x,y)
        GLUtils.disableStandardItemLighting()
        GlStateManager.popMatrix()
    }

    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun drawRect(x: Float, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x.toDouble(), y)
        GL11.glVertex2d(x.toDouble(), y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    fun glColor(color: Color) {
        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255f
        val red = (hex shr 16 and 0xFF) / 255f
        val green = (hex shr 8 and 0xFF) / 255f
        val blue = (hex and 0xFF) / 255f
        GlStateManager.color(red, green, blue, alpha)
    }
}

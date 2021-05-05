package me.kiras.aimwhere.viaversion
import me.kiras.aimwhere.viaversion.util.ProtocolSorter
import me.kiras.aimwhere.viaversion.util.ProtocolUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.opengl.GL11
import java.io.IOException

class ProtocolSelectorScreen(private val parent: GuiScreen) : GuiScreen() {
    var list: SlotList? = null
    override fun initGui() {
        super.initGui()
        buttonList.add(GuiButton(1, width / 2 - 100, height - 27, 200, 20, "Back"))
        list = SlotList(mc, width, height, 32, height - 32, 10)
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        list!!.actionPerformed(button)
        if (button.id == 1) mc.displayGuiScreen(parent)
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        list!!.handleMouseInput()
        super.handleMouseInput()
    }

    override fun drawScreen(drawScreen: Int, mouseX: Int, mouseY: Float) {
        list!!.drawScreen(drawScreen, mouseX, mouseY)
        GL11.glPushMatrix()
        GL11.glScalef(2.0f, 2.0f, 2.0f)
        drawCenteredString(fontRendererObj, EnumChatFormatting.BOLD.toString() + "Version", width / 4, 6, 16777215)
        GL11.glPopMatrix()
        super.drawScreen(drawScreen, mouseX, mouseY)
    }

    inner class SlotList(
        p_i1052_1_: Minecraft?,
        p_i1052_2_: Int,
        p_i1052_3_: Int,
        p_i1052_4_: Int,
        p_i1052_5_: Int,
        p_i1052_6_: Int
    ) :
        GuiSlot(p_i1052_1_, p_i1052_2_, p_i1052_3_, p_i1052_4_, p_i1052_5_, p_i1052_6_) {
        override fun getSize(): Int {
            return ProtocolSorter.getProtocolVersions().size
        }

        override fun elementClicked(i: Int, b: Boolean, i1: Int, i2: Int) {
            ViaVersion.clientSideVersion = ProtocolSorter.getProtocolVersions()[i].version
        }

        override fun isSelected(i: Int): Boolean {
            return false
        }

        override fun drawBackground() {
            drawDefaultBackground()
        }

        override fun drawSlot(i: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int) {
            drawCenteredString(
                mc.fontRendererObj,
                (if (ViaVersion.clientSideVersion == ProtocolSorter.getProtocolVersions()[i].version) EnumChatFormatting.GREEN.toString() else EnumChatFormatting.WHITE.toString()) + ProtocolUtils.getProtocolName(
                    ProtocolSorter.getProtocolVersions()[i].version
                ),
                width / 2,
                i2 + 2,
                -1
            )
        }
    }
}
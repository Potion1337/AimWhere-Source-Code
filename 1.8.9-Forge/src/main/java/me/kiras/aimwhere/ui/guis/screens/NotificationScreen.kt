package me.kiras.aimwhere.ui.guis.screens
import me.kiras.aimwhere.utils.render.BlurUtil
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import java.awt.Color

class NotificationScreen(private val prevScreen: GuiScreen) : GuiScreen() {
    override fun initGui() {
//        BlurUtil.blurAll(0.5F)
//        mc.entityRenderer.loadShader(ResourceLocation("AimWhere/blur.json"))
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
//        BlurUtil.blurAll(1F)
//        BlurUtil.blurShape(height / 2 - 80F, width / 2 - 80, width / 2 - 51, height / 2 - 51,1F)
//        BlurUtil.blurAreaBoarder(height / 2 - 80F, width / 2 - 80F, width / 2 - 51F, height / 2 - 51F,1F, 50F, 50F)
        drawRect(height / 2 - 50, width / 2 - 50, height / 2 + 50, width / 2 + 50, Color(255,255,255).rgb)
        super.drawScreen(mouseX, mouseY, partialTicks)
            BlurUtil.blurAll(1F)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE)
            mc.displayGuiScreen(prevScreen)
    }

}
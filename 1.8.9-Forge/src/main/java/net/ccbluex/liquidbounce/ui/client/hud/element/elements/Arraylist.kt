package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.kiras.aimwhere.utils.render.ColorManager
import me.kiras.aimwhere.utils.render.Palette
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Horizontal
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Vertical
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowShader
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.abs

/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "ArrayList", single = true)
class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                side: Side = Side(Horizontal.RIGHT, Vertical.UP)) : Element(x, y, scale, side) {
    private val nameBreakValue = BoolValue("NameBreak", true)
    private val outLineRectWidth = IntegerValue("OutLineRectWidth", 3, 3, 6)
    private val animationSpeedValue = FloatValue("AnimationSpeed", 0.08F, 0.01F, 0.5F)
    private val rainbow3Offset = IntegerValue("RainbowOffset", 16, 1, 30)
    private val rainbowSpeed = IntegerValue("RainbowSpeed",10,1,10)
    private val rainbowSaturation = FloatValue("RainbowSaturation", 0.5F,0.01F,1F)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom", "Random", "Rainbow","Rainbow2", "Rainbow3","CustomRainbow", "Flux","Astolfo"), "Astolfo")
    private val colorRedValue = IntegerValue("Text-R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("Text-G", 111, 0, 255)
    private val colorBlueValue = IntegerValue("Text-B", 255, 0, 255)
    private val rectColorModeValue = ListValue("Rect-Color", arrayOf("Custom", "Random", "Rainbow","Rainbow2", "Rainbow3", "CustomRainbow", "Flux","Astolfo"), "Astolfo")
    private val rectColorRedValue = IntegerValue("Rect-R", 255, 0, 255)
    private val rectColorGreenValue = IntegerValue("Rect-G", 255, 0, 255)
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Rect-Alpha", 255, 0, 255)
    private val saturationValue = FloatValue("Random-Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Random-Brightness", 1f, 0f, 1f)
    private val tags = BoolValue("Tags", true)
    private val shadow = BoolValue("ShadowText", true)
    private val backgroundColorModeValue = ListValue("Background-Color", arrayOf("Custom", "Random", "Rainbow"), "Custom")
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 0, 0, 255)
    private val rectValue = ListValue("Rect", arrayOf("None", "Left", "Right", "OutLine"), "None")
    private val upperCaseValue = BoolValue("UpperCase", false)
    private val spaceValue = FloatValue("Space", 0F, 0F, 5F)
    private val textHeightValue = FloatValue("TextHeight", 11F, 1F, 20F)
    private val textYValue = FloatValue("TextY", 1F, 0F, 20F)
    private val tagsArrayColor = BoolValue("TagsArrayColor", false)
    private val fontValue = FontValue("Font", Fonts.font40)

    private var x2 = 0
    private var y2 = 0F

    private var modules = emptyList<Module>()

    override fun drawElement(): Border? {
        val fontRenderer = fontValue.get()
        val counter = intArrayOf(0)
        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in LiquidBounce.moduleManager.modules) {
            if (!module.array || (!module.state && module.slide == 0F)) continue

            var displayString = if (!tags.get())
                module.getBreakName(nameBreakValue.get())
            else if (tagsArrayColor.get())
                module.colorlessTagName(nameBreakValue.get())
            else module.tagName(nameBreakValue.get())

            if (upperCaseValue.get())
                displayString = displayString.toUpperCase()

            val width = fontRenderer.getStringWidth(displayString)

            if (module.state) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4F
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4F
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val colorMode = colorModeValue.get()
        val rectColorMode = rectColorModeValue.get()
        val backgroundColorMode = backgroundColorModeValue.get()
        val customColor = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), 1)
        val rectCustomColor = Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
            rectColorBlueAlpha.get())
        val space = spaceValue.get()
        val textHeight = textHeightValue.get()
        val textY = textYValue.get()
        val rectMode = rectValue.get()
        val backgroundCustomColor = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
            backgroundColorBlueValue.get(), backgroundColorAlphaValue.get())
        val textShadow = shadow.get()
        val textSpacer = textHeight + space
        val saturation = saturationValue.get()
        val brightness = brightnessValue.get()


        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                modules.forEachIndexed { index, module ->
                    val translate = module.translate
                    var displayString = if (!tags.get())
                        module.getBreakName(nameBreakValue.get())
                    else if (tagsArrayColor.get())
                        module.colorlessTagName(nameBreakValue.get())
                    else module.tagName(nameBreakValue.get())

                    if (upperCaseValue.get())
                        displayString = displayString.toUpperCase()

                    val xPos = -module.slide - 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb
                    module.translate.interpolate(xPos, yPos, animationSpeedValue.get().toDouble())
                    counter[0] = counter[0] + 1
                    val rectX = xPos - if (rectMode.equals("right", true)) 5 else 2
                    val customRainbowColour = Palette.fade2(customColor,modules.indexOf(module),fontRenderer.FONT_HEIGHT).rgb
                    val customRectRainbowColor = Palette.fade2(rectCustomColor,modules.indexOf(module),fontRenderer.FONT_HEIGHT).rgb

                    RainbowShader.begin(backgroundColorMode.equals("Rainbow", ignoreCase = true), if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        RenderUtils.drawRect(
                            xPos - when (rectMode) {
                                "Right" -> {
                                    5
                                }
                                "OutLine" -> {
                                    outLineRectWidth.get()
                                }
                                else -> {
                                    2
                                }
                            },
                            module.translate.y,
                            if (rectMode.equals("right", true)) -3F else 0F,
                            module.translate.y + textHeight, when {
                                backgroundColorMode.equals("Rainbow", ignoreCase = true) -> 0xFF shl 24
                                backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                else -> backgroundCustomColor.rgb
                            }
                        )
                    }
                    RainbowFontShader.begin(colorMode == "Rainbow", if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        fontRenderer.drawString(displayString, xPos - if (rectMode.equals("right", true)) 3 else 0, translate.y + textY,
                            when(colorMode.toLowerCase()) {
                                "rainbow" -> 0
                                "random" -> moduleColor
                                "customrainbow" -> customRainbowColour
                                "rainbow2" -> ColorUtils.rainbow(400000000L * index,255,rainbowSaturation.get()).rgb
                                "rainbow3" -> ColorManager.getRainbow2(2000, -(translate.y * rainbow3Offset.get().toFloat()).toInt())
                                "flux" -> ColorManager.fluxRainbow(-100, counter[0] * -50 * rainbowSpeed.get().toLong(),rainbowSaturation.get())
                                "astolfo" -> ColorManager.astolfoRainbow(counter[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                                else -> customColor.rgb
                            }, textShadow)
                    }
                    if (!rectMode.equals("none", true)) {
                        RainbowShader.begin(rectMode == "Rainbow", if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                            val rectColor = when(rectColorMode.toLowerCase()) {
                                "rainbow" -> 0
                                "random" -> moduleColor
                                "customrainbow" -> customRectRainbowColor
                                "rainbow2" -> ColorUtils.rainbow(400000000L * index,255,rainbowSaturation.get()).rgb
                                "rainbow3" -> ColorManager.getRainbow2(2000, -(translate.y * rainbow3Offset.get().toFloat()).toInt())
                                "flux" -> ColorManager.fluxRainbow(-100, counter[0] * -50 * rainbowSpeed.get().toLong(),rainbowSaturation.get())
                                "astolfo" -> ColorManager.astolfoRainbow(counter[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                                else -> rectCustomColor.rgb
                            }
                            when(rectMode.toLowerCase()) {
                                "left" -> RenderUtils.drawRect(xPos - 5, translate.y, xPos - 2, translate.y + textHeight,
                                    rectColor)
                                "right" -> RenderUtils.drawRect(-3F, translate.y, 0F,
                                    translate.y + textHeight, rectColor)
                                "outline" -> {
                                    RenderUtils.drawRect(-1F, module.translate.y - 1F, 0F,
                                        module.translate.y + textHeight, rectColor)//右条
                                    RenderUtils.drawRect(xPos - outLineRectWidth.get(), module.translate.y ,xPos - outLineRectWidth.get() + 1, module.translate.y + textHeight,
                                        rectColor)//左条
                                    if (module == modules[0]) {
                                        Gui.drawRect((xPos - outLineRectWidth.get()).toInt(), yPos.toInt(), 0, yPos.toInt() - 1, rectColor) //上条
                                    }
                                    if (module != modules[0]) {
                                        var displayStrings = if (!tags.get())
                                            modules[index - 1].getBreakName(nameBreakValue.get())
                                        else if (tagsArrayColor.get())
                                            modules[index - 1].colorlessTagName(nameBreakValue.get())
                                        else modules[index - 1].tagName(nameBreakValue.get())

                                        if (upperCaseValue.get())
                                            displayStrings = displayStrings.toUpperCase()

                                        RenderUtils.drawRect(xPos - outLineRectWidth.get() - (fontRenderer.getStringWidth(displayStrings) - fontRenderer.getStringWidth(displayString)), module.translate.y, xPos - outLineRectWidth.get() + 1, module.translate.y + 1,
                                            rectColor) //功能左条和下条间隔
                                        if (module == modules[modules.size - 1]) {
                                            RenderUtils.drawRect(xPos - outLineRectWidth.get(), yPos + textHeight, 0.0F, yPos + textHeight + 1,
                                                rectColor) //下条
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Horizontal.LEFT -> {
                modules.forEachIndexed { index, module ->
                    val translate = module.translate
                    var displayString = if (!tags.get())
                        module.getBreakName(nameBreakValue.get())
                    else if (tagsArrayColor.get())
                        module.colorlessTagName(nameBreakValue.get())
                    else module.tagName(nameBreakValue.get())

                    if (upperCaseValue.get())
                        displayString = displayString.toUpperCase()

                    val width = fontRenderer.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + if (rectMode.equals("left", true)) 5 else 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb
                    module.translate.interpolate(xPos, yPos, animationSpeedValue.get().toDouble())
                    counter[0] = counter[0] + 1
                    val customRainbowColour = Palette.fade2(customColor,modules.indexOf(module),fontRenderer.FONT_HEIGHT).rgb
                    val customRectRainbowColor = Palette.fade2(rectCustomColor,modules.indexOf(module),fontRenderer.FONT_HEIGHT).rgb
                    RainbowShader.begin(backgroundColorMode == "Rainbow", if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        RenderUtils.drawRect(
                            0F,
                            yPos,
                            xPos + width + when (rectMode) {
                                "Right" -> {
                                    5
                                }
                                "OutLine" -> {
                                    outLineRectWidth.get() - width
                                }
                                else -> {
                                    2
                                }
                            },
                            yPos + textHeight, when (backgroundColorMode) {
                                "Rainbow" -> 0
                                "Random" -> moduleColor
                                else -> backgroundCustomColor.rgb
                            }
                        )
                    }
                    RainbowFontShader.begin(colorMode == "Rainbow", if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        fontRenderer.drawString(displayString, xPos, translate.y + textY, when(colorMode.toLowerCase()) {
                            "rainbow" -> ColorUtils.rainbow(400000000L * index).rgb
                            "random" -> moduleColor
                            "customrainbow" -> customRainbowColour
                            "rainbow2" -> ColorUtils.rainbow(400000000L * index,255,rainbowSaturation.get()).rgb
                            "rainbow3" -> ColorManager.getRainbow2(2000, -(translate.y * rainbow3Offset.get().toFloat()).toInt())
                            "flux" -> ColorManager.fluxRainbow(-100, counter[0] * -50 * rainbowSpeed.get().toLong(),rainbowSaturation.get())
                            "astolfo" -> ColorManager.astolfoRainbow(counter[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                            else -> customColor.rgb
                        }, textShadow)
                    }
                    if (!rectMode.equals("none", true)) {
                        RainbowFontShader.begin(colorMode == "Rainbow", if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                            val rectColor = when (rectColorMode.toLowerCase()) {
                                "rainbow" -> 0
                                "random" -> moduleColor
                                "customrainbow" -> customRectRainbowColor
                                "rainbow2" -> ColorUtils.rainbow(400000000L * index, 255, rainbowSaturation.get()).rgb
                                "rainbow3" -> ColorManager.getRainbow2(
                                    2000,
                                    -(translate.y * rainbow3Offset.get().toFloat()).toInt()
                                )
                                "flux" -> ColorManager.fluxRainbow(
                                    -100,
                                    counter[0] * -50 * rainbowSpeed.get().toLong(),
                                    rainbowSaturation.get()
                                )
                                "astolfo" -> ColorManager.astolfoRainbow(
                                    counter[0] * 100,
                                    astolfoRainbowOffset.get(),
                                    astolfoRainbowIndex.get()
                                )
                                else -> rectCustomColor.rgb
                            }

                            when {
                                rectMode.equals("left", true) -> RenderUtils.drawRect(
                                    0F,
                                    translate.y - 1, 3F, translate.y + textHeight, rectColor
                                )
                                rectMode.equals("right", true) ->
                                    RenderUtils.drawRect(
                                        xPos + width + 2, translate.y, xPos + width + 2 + 3,
                                        translate.y + textHeight, rectColor
                                    )
                            }
                        }
                    }
                }
            }
        }

        // Draw border
        if (mc.currentScreen is GuiHudDesigner) {
            x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Horizontal.LEFT)
                    Border(0F, -1F, 20F, 20F)
                else
                    Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Horizontal.RIGHT, Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 14
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            y2 = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(0F, 0F, x2 - 7F, y2 - if(side.vertical == Vertical.DOWN) 1F else 0F)
        }

        AWTFontRenderer.assumeNonVolatile = false
        GlStateManager.resetColor()
        return null
    }

    override fun updateElement() {
        modules = LiquidBounce.moduleManager.modules
            .filter { it.array && it.slide > 0 }
            .sortedBy { -fontValue.get().getStringWidth(if (upperCaseValue.get()) (if (!tags.get()) it.getBreakName(nameBreakValue.get()) else if (tagsArrayColor.get()) it.colorlessTagName(nameBreakValue.get()) else it.tagName(nameBreakValue.get())).toUpperCase() else if (!tags.get()) it.getBreakName(nameBreakValue.get()) else if (tagsArrayColor.get()) it.colorlessTagName(nameBreakValue.get()) else it.tagName(nameBreakValue.get())) }
    }
}

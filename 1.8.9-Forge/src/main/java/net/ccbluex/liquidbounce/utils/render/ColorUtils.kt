
package net.ccbluex.liquidbounce.utils.render

import net.minecraft.util.ChatAllowedCharacters
import java.awt.Color
import java.util.*
import java.util.regex.Pattern


object ColorUtils {

    @kotlin.jvm.JvmStatic
    private val COLOR_PATTERN = Pattern.compile("(?i)ยง[0-9A-FK-OR]")

    @JvmField
    val hexColors = IntArray(16)

    init {
        repeat(16) { i ->
            val baseColor = (i shr 3 and 1) * 85

            val red = (i shr 2 and 1) * 170 + baseColor + if (i == 6) 85 else 0
            val green = (i shr 1 and 1) * 170 + baseColor
            val blue = (i and 1) * 170 + baseColor

            hexColors[i] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
        }
    }

    fun rainbow2(offset: Long, fade: Float): Color? {
        val hue = (System.nanoTime() + offset).toFloat() / 1.0E10f % 1.0f
        val color = Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)).toLong(16)
        val c = Color(color.toInt())
        return Color(c.red.toFloat() / 255.0f * fade, c.green.toFloat() / 255.0f * fade, c.blue.toFloat() / 255.0f * fade, c.alpha.toFloat() / 255.0f)
    }

    @JvmStatic
    fun getHealthColor(health: Float, maxHealth: Float): Color? {
        val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
        val colors = arrayOf(Color(108, 0, 0), Color(255, 51, 0), Color.GREEN)
        val progress = health / maxHealth
        return blendColors(fractions, colors, progress).brighter()
    }

    @JvmStatic
    fun stripColor(input: String?): String? {
        return COLOR_PATTERN.matcher(input ?: return null).replaceAll("")
    }
    @JvmStatic
    fun getRainbow(speed: Int, offset: Int): Int {
        var hue = ((System.currentTimeMillis() + offset.toLong()) % speed.toLong()).toFloat()
        hue /= speed.toFloat()
        return Color.getHSBColor(hue, 1.0f, 1.0f).rgb
    }

    fun blend(color1: Color, color2: Color, ratio: Double): Color {
        val r = ratio.toFloat()
        val ir = 1.0f - r
        val rgb1 = FloatArray(3)
        val rgb2 = FloatArray(3)
        color1.getColorComponents(rgb1)
        color2.getColorComponents(rgb2)
        return Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir)
    }

    fun blendColors(fractions: FloatArray, colors: Array<Color>, progress: Float): Color {
        if (fractions.size == colors.size) {
            val indices: IntArray = getFractionIndices(fractions, progress)
            val range = floatArrayOf(fractions[indices[0]], fractions[indices[1]])
            val colorRange = arrayOf(colors[indices[0]], colors[indices[1]])
            val max = range[1] - range[0]
            val value = progress - range[0]
            val weight = value / max
            return blend(colorRange[0], colorRange[1], 1.0 - weight)
        }
        throw IllegalArgumentException("Fractions and colours must have equal number of elements")
    }

    fun getFractionIndices(fractions: FloatArray, progress: Float): IntArray {
        var startPoint: Int
        val range = IntArray(2)
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

    @JvmStatic
    fun translateAlternateColorCodes(textToTranslate: String): String {
        val chars = textToTranslate.toCharArray()

        for (i in 0 until chars.size - 1) {
            if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(chars[i + 1], true)) {
                chars[i] = '§'
                chars[i + 1] = Character.toLowerCase(chars[i + 1])
            }
        }

        return String(chars)
    }

    fun randomMagicText(text: String): String {
        val stringBuilder = StringBuilder()
        val allowedCharacters = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"

        for (c in text.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                val index = Random().nextInt(allowedCharacters.length)
                stringBuilder.append(allowedCharacters.toCharArray()[index])
            }
        }

        return stringBuilder.toString()
    }

    @JvmStatic
    fun rainbow(): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + 400000L) / 10000000000F % 1, 1F, 1F))
        return Color(currentColor.red / 255F * 1F, currentColor.green / 255f * 1F, currentColor.blue / 255F * 1F, currentColor.alpha / 255F)
    }

    // TODO: Use kotlin optional argument feature

    @JvmStatic
    fun rainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 1F, 1F))
        return Color(currentColor.red / 255F * 1F, currentColor.green / 255F * 1F, currentColor.blue / 255F * 1F,
                currentColor.alpha / 255F)
    }

    @JvmStatic
    fun rainbow3(offset: Long, rainbowSpeed: Float, rainbowBright: Float): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, rainbowSpeed, rainbowBright))
        return Color(currentColor.red.toFloat() / 255.0f * 1.0f, currentColor.green.toFloat() / 255.0f * 1.0f, currentColor.blue.toFloat() / 255.0f * 1.0f, currentColor.alpha.toFloat() / 255.0f)
    }

    @JvmStatic
    fun rainbowW(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.6F, 1F))
        return Color(0F, currentColor.red.toFloat() / 255.0F * 1.0F, currentColor.blue.toFloat() / 255.0F * 1.0F, currentColor.alpha.toFloat() / 255.0F)
    }

    @JvmStatic
    fun redRainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.5F, 1F))
        return Color(currentColor.red.toFloat() / 255.0F * 1.0F, 0F, 0F, currentColor.alpha.toFloat() / 255.0F)
    }

    @JvmStatic
    fun greenRainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.5F, 1F))
        return Color(0F, currentColor.green / 255.0F * 1.0F, 0F, currentColor.alpha.toFloat() / 255.0F)
    }

    @JvmStatic
    fun blueRainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.5F, 1F))
        return Color(0F, 0F, currentColor.blue.toFloat() / 255.0F * 1.0F, currentColor.alpha.toFloat() / 255.0F)
    }


    @JvmStatic
    fun rainbow(alpha: Float) = rainbow(400000L, alpha,0.5F)

    @JvmStatic
    fun rainbow(alpha: Int) = rainbow(400000L, alpha / 255,0.5F)

    @JvmStatic
    fun rainbow(offset: Long, alpha: Int, saturation: Float) = rainbow(offset, alpha.toFloat() / 255,saturation)
    @JvmStatic
    fun rainbow(offset: Long, alpha: Float, saturation: Float): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() - offset) / 7500000000F % 1, saturation,1F))
        return Color(currentColor.red / 255F, currentColor.green / 255f, currentColor.blue / 255F, alpha)
    }
    @JvmStatic
    fun newRainbow(speed: Float, offset: Float, speed2: Float) : Int{//int speed:int offset:int
        var color = (Date().time + offset) % speed;//float
        color /= speed
        return Color(Color.HSBtoRGB(color, 0.55F, speed2)).rgb
    }

    @JvmStatic
    fun otherRainbow(offset: Float, rainbowSpeed: Float) : Int {
        val currentColor = Color(Color.HSBtoRGB(offset, rainbowSpeed, 1F)).rgb
        return currentColor
    }

    @JvmStatic
    fun reAlpha(color: Color,alpha: Int): Color{
        return Color(color.red,color.green,color.blue,alpha)
    }
}

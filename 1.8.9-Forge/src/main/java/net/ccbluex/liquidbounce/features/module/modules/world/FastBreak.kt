/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(
    name = "SpeedMine",
    description = "Allows you to break blocks faster.",
    category = ModuleCategory.WORLD
)
class FastBreak : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Normal","Hypixel"), "Hypixel");
    private val breakDamage = FloatValue("NormalBreakDamage", 0.8F, 0.1F, 1F)
    private val breakSpeedValue = FloatValue("HypixelBreakSpeed", 1.4F, 1F, 2F);
    private var bzs = false
    private var bzx = 0.0F
    var blockPos: BlockPos? = null
    private var facing: EnumFacing? = null
    override val tag: String
        get() = modeValue.get()
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when(modeValue.get()) {
            "Hypixel" -> {
                if (mc.playerController.extendedReach()) {
                    mc.playerController.blockHitDelay = 0
                } else if (bzs) {
                    val block = mc.theWorld.getBlockState(blockPos).block
                    bzx += (block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos).toDouble() * breakSpeedValue.value).toFloat()
                    if (bzx >= 1.0F) {
                        mc.theWorld.setBlockState(blockPos, Blocks.air.defaultState, 11)
                        mc.netHandler.networkManager.sendPacket(C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, facing))
                        bzx = 0.0F
                        bzs = false
                    }
                }
            }
            "Normal" -> {
                mc.playerController.blockHitDelay = 0
                if (mc.playerController.curBlockDamageMP > breakDamage.get())
                    mc.playerController.curBlockDamageMP = 1F
                if (Fucker.currentDamage > breakDamage.get())
                    Fucker.currentDamage = 1F
                if (Nuker.currentDamage > breakDamage.get())
                    Nuker.currentDamage = 1F
            }
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(modeValue.get() == "Hypixel") {
            if (event.packet is C07PacketPlayerDigging && !mc.playerController.extendedReach()
                    && mc.playerController != null) {
                val c07PacketPlayerDigging = event.packet
                if (c07PacketPlayerDigging.status == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    bzs = true
                    blockPos = c07PacketPlayerDigging.position
                    facing = c07PacketPlayerDigging.facing
                    bzx = 0.0f
                } else if (c07PacketPlayerDigging.status == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
                        || c07PacketPlayerDigging.status == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                    bzs = false
                    blockPos = null
                    facing = null
                }
            }
        }
    }
}

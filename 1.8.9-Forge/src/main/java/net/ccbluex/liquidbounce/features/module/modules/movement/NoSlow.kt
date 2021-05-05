
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.SlowDownEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing


@ModuleInfo(name = "NoSlow",
        description = "Cancels slowness effects caused by soulsand and using items.",
        category = ModuleCategory.MOVEMENT
)
class NoSlow : Module() {
    private val msTimer = MSTimer()
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customOnGround = BoolValue("CustomOnGround", false)
    private val customDelayValue = IntegerValue("CustomDelay",60,10,200)
    private val customSendC08Value = BoolValue("CustomBlock", true)
    private val packetModeValue = ListValue("Packet", arrayOf("AntiCheat","Custom","NCP","Vanilla","AAC"), "NCP")
    // Soulsand
    val soulsandValue = BoolValue("Soulsand", true)

    override fun onDisable() {
        msTimer.reset()
    }

    override val tag: String
        get() = packetModeValue.get()

    private fun sendPacket(Event : MotionEvent,SendC07 : Boolean, SendC08 : Boolean,Delay : Boolean,DelayValue : Long,onGround : Boolean,Hypixel : Boolean = false) {
        val killAura = LiquidBounce.moduleManager[KillAura::class.java]
        val digging = C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1,-1,-1), EnumFacing.DOWN)
        val blockPlace = C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
        val blockMent = C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f)
        if(onGround && !mc.thePlayer.onGround) {
            return
        }
        if(SendC07 && Event.eventState == EventState.PRE) {
            if(Delay && msTimer.hasTimePassed(DelayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if(!Delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if(SendC08 && Event.eventState == EventState.POST) {
            if(killAura.noBlock)
                return
            if(Delay && msTimer.hasTimePassed(DelayValue) && !Hypixel) {
                mc.netHandler.addToSendQueue(blockPlace)
                msTimer.reset()
            } else if(!Delay && !Hypixel) {
                mc.netHandler.addToSendQueue(blockPlace)
            } else if(Hypixel) {
                mc.netHandler.addToSendQueue(blockMent)
            }
        }
    }


    @EventTarget
    fun onMotion(event: MotionEvent) {
        val heldItem = mc.thePlayer.heldItem
        if (heldItem == null || heldItem.item !is ItemSword || !MovementUtils.isMoving()) {
            return
        }
        val killAura = LiquidBounce.moduleManager[KillAura::class.java]
        if (!mc.thePlayer.isBlocking && !killAura.blockingStatus) {
            return
        }
        val packet = mc.thePlayer.ticksExisted % 2 == 0
        when(packetModeValue.get().toLowerCase()) {
            "anticheat" -> {
                this.sendPacket(event, SendC07 = true, SendC08 = packet, Delay = false, DelayValue = 0, onGround = false, Hypixel = false)
            }
            "aac" -> this.sendPacket(event,!packet,packet,Delay = false,DelayValue = 0, onGround = false, Hypixel = false)
            "ncp" -> sendPacket(event, SendC07 = true, SendC08 = true, Delay = false, DelayValue = 0, onGround = false, Hypixel = false)
            "custom" -> {
                sendPacket(event,SendC07 = true,SendC08 = customSendC08Value.get(),Delay = true,DelayValue = customDelayValue.get().toLong(),onGround = customOnGround.get(),Hypixel = false)
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem?.item
        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> {
            if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
        }
        is ItemSword -> {
            if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
        }
        is ItemBow -> {
            if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
        }
        else -> 0.2F
    }

}

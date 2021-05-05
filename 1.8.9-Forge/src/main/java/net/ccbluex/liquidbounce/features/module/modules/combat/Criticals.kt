package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.util.*
import kotlin.math.max

@ModuleInfo(
    name = "Criticals",
    description = "Automatically deals critical hits.",
    category = ModuleCategory.COMBAT
)
class Criticals : Module() {

    val modeValue = ListValue("Mode", arrayOf("NewPacket","Hypixel","Packet", "NoGround","AntiCheat","AntiCheat2","MorePacket","RandomPacket","Motion","AAC"), "Packet")
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val randomAntiCheatValue = BoolValue("RandomAntiCheat", false)
    private val antiCheatNoMoveValue = BoolValue("AntiCheatNoMove", true)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val motionYValue = FloatValue("MotionY", 0.42F, 0.01F,0.42F)
    private val minRandomPacketValue : FloatValue = object : FloatValue("MinRandomPacket",0.000000000000000122774287821489214F,0F,0.0625F) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            if(newValue > maxRandomPacketValue.get())
                set(maxRandomPacketValue.get())
            if(newValue < minimum)
                set(minimum)
            if(newValue > maximum)
                set(maximum)
        }
    }
    private val maxRandomPacketValue : FloatValue = object : FloatValue("MaxRandomPacket", 0.00000000000042638121217822151F, 0F, 0.0625F) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            if(newValue < minRandomPacketValue.get())
                set(minRandomPacketValue.get())
            if(newValue < minimum)
                set(minimum)
            if(newValue > maximum)
                set(maximum)
        }
    }
    val msTimer = MSTimer()
    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            val thePlayer = mc.thePlayer ?: return
            val entity = event.targetEntity ?: return

            if (!thePlayer.onGround || thePlayer.isOnLadder || thePlayer.isInWeb || thePlayer.isInWater ||
                    thePlayer.isInLava || thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                    LiquidBounce.moduleManager[Fly::class.java].state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return

            val x = thePlayer.posX
            val y = thePlayer.posY
            val z = thePlayer.posZ
            when (modeValue.get().toLowerCase()) {
                "aac" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.05250000001304,z, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.01400000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    mc.thePlayer.onCriticalHit(entity)
                }
                "morepacket" -> {
                    for(i in doubleArrayOf(0.000000000000000122774287821489214, 0.00000000000000012663166169963169,0.00000000000000000000000021028028021482194024124,1.214361E-10)) {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x,y + i,z,false))
                    }
                }
                "motion" -> thePlayer.motionY = motionYValue.get().toDouble()
                "randompacket" -> {
                    mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(x, y + RandomUtils.nextDouble(minRandomPacketValue.get().toDouble(), maxRandomPacketValue.get().toDouble()), z, false))
                    mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(x, y,z, false))
                }
                "anticheat" -> {
                    if(!randomAntiCheatValue.get() || Random().nextBoolean()) {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.0000000000000036, z, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    }
                }
                "anticheat2" -> {
                    if (!randomAntiCheatValue.get() || Random().nextBoolean()) {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00000000000002593, z, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    }
                }
                "hypixel" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.05250999867916107, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.3331999936342235, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.0010999999940395355, z, false))
                }
                "newpacket" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.05250000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                }
                "packet" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.062487826, z, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.04, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.0012016413, z, false))
                }
            }
            thePlayer.onCriticalHit(entity)
            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer && modeValue.get().equals("NoGround", ignoreCase = true))
            packet.onGround = false
    }

    override val tag: String
        get() = modeValue.get()
}
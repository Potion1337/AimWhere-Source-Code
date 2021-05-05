package net.ccbluex.liquidbounce.features.module.modules.combat

import me.kiras.aimwhere.utils.math.Vec4
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(
    name = "InfiniteHit",
    description = "Allows to hit entities from far away.",
    category = ModuleCategory.COMBAT
)
class TeleportHit : Module() {
    private val swing = BoolValue("Swing", true)
    private val autoAttack = BoolValue("AutoAttack", false)
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)
            delay = TimeUtils.randomClickDelay(minCPS.get(),this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 9, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)
            delay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }
    private val type = ListValue("Type", arrayOf("Pre", "Post"), "Pre")
    private var delay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
    private val msTimer = MSTimer()
    private var targetEntity: EntityLivingBase? = null
    private var path = ArrayList<Vec4?>()
    private var isBlocking = false
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState != EventState.PRE && type.get().equals("Pre", ignoreCase = true) || event.eventState != EventState.POST && type.get().equals("Post", ignoreCase = true)) return
        val facedEntity = RaycastUtils.raycastEntity(100.0) { raycastedEntity: Entity? -> raycastedEntity is EntityLivingBase }
        if ((mc.gameSettings.keyBindAttack.isKeyDown || autoAttack.get()) && EntityUtils.isSelected(facedEntity, true)) targetEntity = facedEntity as EntityLivingBase
        if (mc.thePlayer.canEntityBeSeen(targetEntity) && msTimer.hasTimePassed(delay)) {
            isBlocking = mc.thePlayer.isBlocking
            val topFrom = Vec4(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
            val to = Vec4(targetEntity!!.posX, targetEntity!!.posY, targetEntity!!.posZ)
            path = PathUtils.computePath(topFrom, to)
            for (i in path)
                mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(i!!.x, i.y, i.z, true))
            if (isBlocking)
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            if (swing.get()) mc.thePlayer.swingItem() else mc.netHandler.addToSendQueue(C0APacketAnimation())
            mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK))
            mc.thePlayer.onCriticalHit(targetEntity)
            path.reverse()
            for (i in path)
                mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(i!!.x, i.y, i.z, true))
            if (isBlocking)
                mc.netHandler.networkManager.sendPacket(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.heldItem, 0F, 0F, 0F))
            delay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
            msTimer.reset()
            targetEntity = null
        }
    }
}
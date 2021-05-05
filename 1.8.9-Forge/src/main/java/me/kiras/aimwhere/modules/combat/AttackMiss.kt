package me.kiras.aimwhere.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
@ModuleInfo(
        "AttackMiss",
        "Cancel the Attack Packet.",
        ModuleCategory.COMBAT
)
class AttackMiss : Module() {
    private val rangeValue = FloatValue("Range", 3.5F, 3.1F, 4.5F)
    private var limitPacket = false
    private var flag = 0
    @EventTarget
    fun onAttack(event: AttackEvent) {
        val target = event.targetEntity as EntityLivingBase
        if (mc.thePlayer.getDistanceToEntity(target) > rangeValue.get()) {
            limitPacket = true
            ++flag
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(flag >= 40)
            flag = 0
    }

    @EventTarget
    private fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if(packet is C02PacketUseEntity && packet.action == C02PacketUseEntity.Action.ATTACK) {
            if(limitPacket && flag >= 6)
                event.cancelEvent()
            limitPacket = false
        }
    }
}
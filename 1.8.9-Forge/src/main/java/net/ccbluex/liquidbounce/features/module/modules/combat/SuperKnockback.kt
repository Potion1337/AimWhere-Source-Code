
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(
    name = "SuperKnockBack",
    description = "Increases knockback dealt to other entities.",
    category = ModuleCategory.COMBAT
)
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val delayValue = IntegerValue("Delay", 0, 0, 1000)
    private val timer = MSTimer()
    override val tag: String
        get() = "Hypixel"
    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if(delayValue.get() != 0 && !timer.hasTimePassed(delayValue.get().toLong()))
                return
             else
                timer.reset()
            if (event.targetEntity.hurtTime > hurtTimeValue.get())
                return
            if (mc.thePlayer.isSprinting)
                mc.thePlayer.isSprinting = false
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer,C0BPacketEntityAction.Action.START_SPRINTING))
            mc.thePlayer.isSprinting = true
            mc.thePlayer.serverSprintState = true
        }
    }
}
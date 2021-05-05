package net.ccbluex.liquidbounce.features.module.modules.combat

import me.kiras.aimwhere.utils.timer.TimerUtil
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C16PacketClientStatus

@ModuleInfo(
        name = "AutoArmor",
        description = "Automatically equips the best armor in your inventory.",
        category = ModuleCategory.COMBAT
)
class AutoArmor : Module() {
    private val timer = TimerUtil()
    @EventTarget
    fun onTick(event: TickEvent) {
        val delay: Long = DELAY.get().toLong() * 50
        if (MODE.get() == "OpenInv" && mc.currentScreen !is GuiInventory) {
            return
        }
        if (mc.currentScreen == null || mc.currentScreen is GuiInventory || mc.currentScreen is GuiChat) {
            if (timer.hasReached(delay.toDouble())) {
                bestArmor
            }
        }
    }

    private val bestArmor: Unit
        get() {
            for (type in 1..4) {
                if (mc.thePlayer.inventoryContainer.getSlot(4 + type).hasStack) {
                    val `is` = mc.thePlayer.inventoryContainer.getSlot(4 + type).stack
                    if (isBestArmor(`is`, type)) {
                        continue
                    } else {
                        if (MODE.get() == "FakeInv") {
                            val p = C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)
                            mc.thePlayer.sendQueue.addToSendQueue(p)
                        }
                        drop(4 + type)
                    }
                }
                for (i in 9..44) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                        val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
                        if (isBestArmor(`is`, type) && getProtection(`is`) > 0) {
                            shiftClick(i)
                            timer.reset()
                            if (DELAY.get().toLong() > 0) return
                        }
                    }
                }
            }
        }

    private fun shiftClick(slot: Int) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer)
    }

    private fun drop(slot: Int) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer)
    }

    override val tag: String
        get() = MODE.get()

    companion object {
        var DELAY = IntegerValue("Delay", 1, 0, 10)
        var MODE = ListValue("Mode", arrayOf("Basic", "OpenInv", "FakeInv"), "Basic")
        fun isBestArmor(stack: ItemStack, type: Int): Boolean {
            val prot = getProtection(stack)
            var strType = ""
            when (type) {
                1 -> {
                    strType = "helmet"
                }
                2 -> {
                    strType = "chestplate"
                }
                3 -> {
                    strType = "leggings"
                }
                4 -> {
                    strType = "boots"
                }
            }
            if (!stack.unlocalizedName.contains(strType)) {
                return false
            }
            for (i in 5..44) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                    val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
                    if (getProtection(`is`) > prot && `is`.unlocalizedName.contains(strType)) return false
                }
            }
            return true
        }

        fun getProtection(stack: ItemStack): Float {
            var prot = 0f
            if (stack.item is ItemArmor) {
                val armor = stack.item as ItemArmor
                prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075.toFloat()
                prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0.toFloat()
                prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0.toFloat()
                prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0.toFloat()
                prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0.toFloat()
                prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0.toFloat()
            }
            return prot
        }
    }
}
package me.kiras.aimwhere.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.NoFriends
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "Aura", description = "Auto attack the entities around u.", category = ModuleCategory.COMBAT)
class Aura : Module() {
    private var target : EntityLivingBase ?= null
    private var blockTarget : EntityLivingBase ?= null
    private val cpsValue = IntegerValue("CPS", 12,1,20)
    private val rangeValue = FloatValue("Range", 4.2F,0.1F,8.0F)
    private val blockRangeValue = FloatValue("BlockRange", 5.0F, 0.1F, 8.0F)
    private val autoBlockValue = ListValue("Block", arrayOf("AfterTick","Normal","Off"), "AfterTick")
    private val sortModeValue = ListValue("Sort", arrayOf("Direction","Armor","LivingTime","Distance"), "Armor")
    private val targetModeValue = ListValue("Target", arrayOf("Single","Switch"), "Single")
    private val targetList = ArrayList<EntityLivingBase>()
    private val attackDelayTimer = MSTimer()
    private var isBlocking = false
    private var index = 0
    override fun onDisable() {
        target = null
        index = 0
        isBlocking = false
        if(mc.thePlayer.isBlocking)
            stopBlocking()
        attackDelayTimer.reset()
        targetList.clear()
    }
    private fun startBlocking() {
        isBlocking = true
        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
    }
    private fun stopBlocking() {
        isBlocking = false
        mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN,EnumFacing.DOWN))
    }
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0
    private fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                if (EntityUtils.isFriend(entity) && !LiquidBounce.moduleManager[NoFriends::class.java].state)
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java]

                return !teams.state || !teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals &&
                    EntityUtils.isAnimal(entity)
        }

        return false
    }
    private fun getBlockTarget(): EntityLivingBase? {
        val targets = java.util.ArrayList<EntityLivingBase>()
        targets.clear()
        val entityList = mc.theWorld.loadedEntityList
        for(i in entityList) {
            if(i is EntityLivingBase && isEnemy(i)) {
                if (mc.thePlayer.getDistanceToEntityBox(i) <= this.blockRangeValue.get()) {
                    targets.add(i)
                    this.blockTarget = i
                }
            }
        }
        return if (targets.isEmpty()) null else targets[0]
    }
    override val tag: String
        get() = targetModeValue.get()
    @EventTarget
    fun onMotion(event : MotionEvent) {
        if(event.eventState == EventState.POST) {
            if(canBlock && !isBlocking && autoBlockValue.get() == "AfterTick" && target != null)
                startBlocking()
            blockTarget = getBlockTarget()
            if(blockTarget != null && target == null && autoBlockValue.get() != "Off" && !isBlocking)
                startBlocking()
            for (i in mc.theWorld.loadedEntityList) {
                if (i is EntityLivingBase && isEnemy(i) && i.getDistanceToEntity(mc.thePlayer) <= rangeValue.get() && !targetList.contains(i))
                    targetList.add(i)
                if (targetList.contains(i) && mc.thePlayer.getDistanceToEntity(i) > rangeValue.get())
                    targetList.remove(i)
            }
            if(targetList.size > 2)
                when(sortModeValue.get()) {
                    "Direction" -> targetList.sortBy { RotationUtils.getRotationDifference(it) }
                    "LivingTime" -> targetList.sortBy{-it.ticksExisted}
                    "Armor" -> targetList.sortBy {it.totalArmorValue}
                    else -> targetList.sortBy { mc.thePlayer.getDistanceToEntity(it) }
                }
        }
    }
    @EventTarget
    private fun onStrafe(event : StrafeEvent) {
        if(RotationUtils.targetRotation != null) {
            RotationUtils.targetRotation.applyStrafeToPlayer(event)
            event.cancelEvent()
        }
    }
    private val canBlock : Boolean
            get() = mc.thePlayer.heldItem.item is ItemSword
    @EventTarget
    fun onUpdate(event : UpdateEvent) {
        if(targetList.isEmpty()) {
            target = null
            return
        }
        target = if(targetModeValue.get() == "Single") targetList[0] else targetList[if(index > targetList.size - 1) 0 else index]
        RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation,RotationUtils.toRotation(RotationUtils.getCenter(target!!.entityBoundingBox),false), 180F))
        if(attackDelayTimer.hasTimePassed(1000L / cpsValue.get().toLong())) {
            val openInventory = mc.currentScreen is GuiInventory
            if(openInventory)
                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
            if(mc.thePlayer.isBlocking || isBlocking)
                stopBlocking()
            mc.thePlayer.swingItem()
            mc.netHandler.addToSendQueue(C02PacketUseEntity(target,C02PacketUseEntity.Action.ATTACK))
            if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder &&
                    !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isRiding)
                mc.thePlayer.onCriticalHit(target)

            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, target!!.creatureAttribute) > 0F)
                mc.thePlayer.onEnchantmentCritical(target)
            if(autoBlockValue.get() == "Normal" && canBlock)
                startBlocking()
            ++index
            if(openInventory)
                mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
            attackDelayTimer.reset()
        }
    }
}
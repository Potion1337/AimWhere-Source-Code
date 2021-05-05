package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.block.BlockUtils.Collidable
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import org.lwjgl.input.Keyboard
import java.util.*

@ModuleInfo(
        name = "LiquidWalk",
        description = "Allows you to walk on water.",
        category = ModuleCategory.MOVEMENT,
        keyBind = Keyboard.KEY_J
)
class LiquidWalk : Module() {
    val modeValue = ListValue("Mode", arrayOf("Vanilla", "NCP", "AAC", "AAC3.3.11", "AACFly", "Spartan", "Dolphin", "Matrix", "Solid","Swim"), "NCP")
    private val noJumpValue = BoolValue("NoJump", false)
    private val aacFlyValue = FloatValue("AACFlyMotion", 0.5f, 0.1f, 1f)
    private var ticks = 0
    private var wasWater = false
    private var nextTick = false
    private fun canJeboos(): Boolean {
        return !(mc.thePlayer.fallDistance >= 3.0f || mc.gameSettings.keyBindJump.isPressed
                        || isInLiquid || mc.thePlayer.isSneaking)
    }

    private fun shouldJesus(): Boolean {
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ
        val pos = ArrayList(
                listOf(BlockPos(x + 0.3, y, z + 0.3), BlockPos(x - 0.3, y, z + 0.3),
                        BlockPos(x + 0.3, y, z - 0.3), BlockPos(x - 0.3, y, z - 0.3)))
        for (po in pos) {
            if (mc.theWorld.getBlockState(po).block !is BlockLiquid) continue
            if (mc.theWorld.getBlockState(po).properties[BlockLiquid.LEVEL] is Int) {
                if (mc.theWorld.getBlockState(po).properties[BlockLiquid.LEVEL] as Int <= 4) {
                    return true
                }
            }
        }
        return false
    }

    override fun onEnable() {
        ticks = 0
        wasWater = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null || mc.thePlayer.isSneaking) return
        when (modeValue.get().toLowerCase()) {
            "solid" -> if (isInLiquid && !mc.thePlayer.isSneaking && !mc.gameSettings.keyBindJump.isPressed) {
                mc.thePlayer.motionY = 0.05
                mc.thePlayer.onGround = true
            }
            "ncp", "vanilla" -> if (collideBlock(mc.thePlayer.entityBoundingBox, object : Collidable {
                        override fun collideBlock(block: Block?): Boolean {
                            return block is BlockLiquid
                        }
                    }) && mc.thePlayer.isInsideOfMaterial(Material.air) && !mc.thePlayer.isSneaking) mc.thePlayer.motionY = 0.08
            "aac" -> {
                val blockPos = mc.thePlayer.position.down()
                if (!mc.thePlayer.onGround && getBlock(blockPos) === Blocks.water || mc.thePlayer.isInWater) {
                    if (!mc.thePlayer.isSprinting) {
                        mc.thePlayer.motionX *= 0.99999
                        mc.thePlayer.motionY *= 0.0
                        mc.thePlayer.motionZ *= 0.99999
                        if (mc.thePlayer.isCollidedHorizontally) mc.thePlayer.motionY = (mc.thePlayer.posY - (mc.thePlayer.posY - 1).toInt()).toInt() / 8f.toDouble()
                    } else {
                        mc.thePlayer.motionX *= 0.99999
                        mc.thePlayer.motionY *= 0.0
                        mc.thePlayer.motionZ *= 0.99999
                        if (mc.thePlayer.isCollidedHorizontally) mc.thePlayer.motionY = (mc.thePlayer.posY - (mc.thePlayer.posY - 1).toInt()).toInt() / 8f.toDouble()
                    }
                    if (mc.thePlayer.fallDistance >= 4) mc.thePlayer.motionY = -0.004 else if (mc.thePlayer.isInWater) mc.thePlayer.motionY = 0.09
                }
                if (mc.thePlayer.hurtTime != 0) mc.thePlayer.onGround = false
            }
            "spartan" -> if (mc.thePlayer.isInWater) {
                if (mc.thePlayer.isCollidedHorizontally) {
                    mc.thePlayer.motionY += 0.15
                    return
                }
                val block = getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ))
                val blockUp = getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.1, mc.thePlayer.posZ))
                if (blockUp is BlockLiquid) {
                    mc.thePlayer.motionY = 0.1
                } else if (block is BlockLiquid) {
                    mc.thePlayer.motionY = 0.0
                }
                mc.thePlayer.onGround = true
                mc.thePlayer.motionX *= 1.085
                mc.thePlayer.motionZ *= 1.085
            }
            "aac3.3.11" -> if (mc.thePlayer.isInWater) {
                mc.thePlayer.motionX *= 1.17
                mc.thePlayer.motionZ *= 1.17
                if (mc.thePlayer.isCollidedHorizontally) mc.thePlayer.motionY = 0.24 else if (mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)).block !== Blocks.air) mc.thePlayer.motionY += 0.04
            }
            "dolphin" -> {
                if (mc.thePlayer.isInWater && shouldJesus()) {
                    mc.thePlayer.motionY = 0.09
                }
                if (mc.thePlayer.onGround || mc.thePlayer.isOnLadder) {
                    wasWater = false
                }
                if (mc.thePlayer.motionY > 0.0 && wasWater) {
                    if (mc.thePlayer.motionY <= 0.11) {
                        mc.thePlayer.motionY *= 1.2671
                    }
                    mc.thePlayer.motionY += 0.05172
                }
                if (isInLiquid) {
                    if (ticks < 3) {
                        mc.thePlayer.motionY = 0.13
                        ++ticks
                        wasWater = false
                    } else {
                        mc.thePlayer.motionY = 0.5
                        ticks = 0
                        wasWater = true
                    }
                }
            }
            "matrix" -> if (mc.thePlayer.isInWater) {
                mc.thePlayer.motionY = 0.28
                Hclip(1.2)
            }
        }
    }

    private val isInLiquid: Boolean
        get() {
            if (mc.thePlayer == null) {
                return false
            }
            for (x in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minX) until MathHelper
                    .floor_double(mc.thePlayer.entityBoundingBox.maxX) + 1) {
                for (z in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minZ) until MathHelper
                        .floor_double(mc.thePlayer.entityBoundingBox.maxZ) + 1) {
                    val pos = BlockPos(x, mc.thePlayer.entityBoundingBox.minY.toInt(), z)
                    val block = mc.theWorld.getBlockState(pos).block
                    if (block != null && block !is BlockAir) {
                        return block is BlockLiquid
                    }
                }
            }
            return false
        }

    fun Hclip(offset: Double) {
        val playerYaw = radians(mc.thePlayer.rotationYaw.toDouble())
        mc.thePlayer.setPosition(mc.thePlayer.posX - Math.sin(playerYaw) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(playerYaw) * offset)
    }

    fun radians(offset: Double): Double {
        return offset * Math.PI / 180
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if ("aacfly" == modeValue.get().toLowerCase() && mc.thePlayer.isInWater) {
            event.y = aacFlyValue.get().toDouble()
            mc.thePlayer.motionY = aacFlyValue.get().toDouble()
        }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (mc.thePlayer == null || mc.thePlayer.entityBoundingBox == null) return
        if (event.block is BlockLiquid && !collideBlock(mc.thePlayer.entityBoundingBox, object : Collidable {
                    override fun collideBlock(block: Block?): Boolean {
                        return block is BlockLiquid
                    }
                }) && !mc.thePlayer.isSneaking) {
            when (modeValue.get().toLowerCase()) {
                "ncp", "vanilla" -> event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.toDouble(), event.y + 1.toDouble(), event.z + 1.toDouble())
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        if (event.packet is C03PacketPlayer) {
            val packetPlayer = event.packet
            if (modeValue.get() == "NCP") {
                if (collideBlock(AxisAlignedBB(mc.thePlayer.entityBoundingBox.maxX, mc.thePlayer.entityBoundingBox.maxY, mc.thePlayer.entityBoundingBox.maxZ, mc.thePlayer.entityBoundingBox.minX, mc.thePlayer.entityBoundingBox.minY - 0.01, mc.thePlayer.entityBoundingBox.minZ), object : Collidable {
                            override fun collideBlock(block: Block?): Boolean {
                                return block is BlockLiquid
                            }
                        })) {
                    nextTick = !nextTick
                    if (nextTick) packetPlayer.y -= 0.001
                }
            }
            if (modeValue.get() == "Solid") {
                if (canJeboos() && isOnLiquid) {
                    packetPlayer.y = if (mc.thePlayer.ticksExisted % 2 == 0) packetPlayer.y + 0.01 else packetPlayer.y - 0.01
                }
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mc.thePlayer == null) return
        val block = getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.01, mc.thePlayer.posZ))
        if (noJumpValue.get() && block is BlockLiquid) event.cancelEvent()
    }

    override val tag: String
        get() = modeValue.get()

    companion object {
        val isOnLiquid: Boolean
            get() {
                var boundingBox: AxisAlignedBB? = mc.thePlayer.entityBoundingBox ?: return false
                boundingBox = boundingBox!!.contract(0.01, 0.0, 0.01).offset(0.0, -0.01, 0.0)
                var onLiquid = false
                val y = boundingBox.minY.toInt()
                for (x in MathHelper.floor_double(boundingBox.minX) until MathHelper
                        .floor_double(boundingBox.maxX + 1.0)) {
                    for (z in MathHelper.floor_double(boundingBox.minZ) until MathHelper
                            .floor_double(boundingBox.maxZ + 1.0)) {
                        val block = mc.theWorld.getBlockState(BlockPos(x, y, z)).block
                        if (block !== Blocks.air) {
                            if (block !is BlockLiquid) {
                                return false
                            }
                            onLiquid = true
                        }
                    }
                }
                return onLiquid
            }
    }
}